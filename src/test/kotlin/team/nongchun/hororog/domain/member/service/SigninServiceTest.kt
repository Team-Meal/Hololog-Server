package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProperties
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.time.LocalDateTime

class SigninServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val jwtProvider = mockk<JwtProvider>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val jwtProperties =
            JwtProperties(
                secret = "test-secret-key-for-hororog-must-be-at-least-32-bytes-long",
                accessExpiration = 1_800_000,
                refreshExpiration = 1_209_600_000,
            )
        val service =
            SigninServiceImpl(
                memberRepository,
                passwordEncoder,
                jwtProvider,
                refreshTokenRepository,
                jwtProperties,
            )

        val request = SigninRequest(email = "nutritionist@hororog.team", password = "password1234")
        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded-password",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        Given("이메일과 비밀번호가 올바를 때") {
            every { memberRepository.findByEmail(request.email) } returns member
            every { passwordEncoder.matches(request.password, member.password) } returns true
            every { jwtProvider.createAccessToken(1L, Role.NUTRITIONIST) } returns "access-token"
            every { jwtProvider.createRefreshToken(1L) } returns "refresh-token"
            every { jwtProvider.getExpiration("access-token") } returns LocalDateTime.of(2026, 6, 11, 12, 30)
            every { jwtProvider.getExpiration("refresh-token") } returns LocalDateTime.of(2026, 6, 25, 12, 0)
            val savedSlot = slot<RefreshToken>()
            every { refreshTokenRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("로그인하면") {
                val response = service.execute(request)

                Then("토큰과 만료일시, role을 반환하고 refresh 토큰을 저장한다") {
                    response.accessToken shouldBe "access-token"
                    response.refreshToken shouldBe "refresh-token"
                    response.accessTokenExpiresAt shouldBe LocalDateTime.of(2026, 6, 11, 12, 30)
                    response.refreshTokenExpiresAt shouldBe LocalDateTime.of(2026, 6, 25, 12, 0)
                    response.role shouldBe Role.NUTRITIONIST

                    verify(exactly = 1) { refreshTokenRepository.save(any()) }
                    savedSlot.captured.userId shouldBe 1L
                    savedSlot.captured.token shouldBe "refresh-token"
                    savedSlot.captured.ttl shouldBe 1_209_600L
                }
            }
        }

        Given("이메일에 해당하는 회원이 없을 때") {
            every { memberRepository.findByEmail(request.email) } returns null

            When("로그인하면") {
                Then("InvalidCredentialsException이 발생한다") {
                    shouldThrow<InvalidCredentialsException> { service.execute(request) }
                    verify(exactly = 0) { refreshTokenRepository.save(any()) }
                }
            }
        }

        Given("비밀번호가 일치하지 않을 때") {
            every { memberRepository.findByEmail(request.email) } returns member
            every { passwordEncoder.matches(request.password, member.password) } returns false

            When("로그인하면") {
                Then("InvalidCredentialsException이 발생한다") {
                    shouldThrow<InvalidCredentialsException> { service.execute(request) }
                    verify(exactly = 0) { refreshTokenRepository.save(any()) }
                }
            }
        }
    })

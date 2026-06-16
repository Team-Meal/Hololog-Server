package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.TokenIssuer
import java.time.LocalDateTime

class SigninServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val tokenIssuer = mockk<TokenIssuer>()
        val service = SigninServiceImpl(memberRepository, passwordEncoder, tokenIssuer)

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
        val signinResponse =
            SigninResponse(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                accessTokenExpiresAt = LocalDateTime.of(2026, 6, 11, 12, 30),
                refreshTokenExpiresAt = LocalDateTime.of(2026, 6, 25, 12, 0),
                role = Role.NUTRITIONIST,
            )

        Given("이메일과 비밀번호가 올바를 때") {
            every { memberRepository.findByEmail(request.email) } returns member
            every { passwordEncoder.matches(request.password, member.password) } returns true
            every { tokenIssuer.issue(member) } returns signinResponse

            When("로그인하면") {
                val response = service.execute(request)

                Then("토큰을 발급해 반환한다") {
                    response shouldBe signinResponse
                    verify(exactly = 1) { tokenIssuer.issue(member) }
                }
            }
        }

        Given("이메일에 해당하는 회원이 없을 때") {
            every { memberRepository.findByEmail(request.email) } returns null

            When("로그인하면") {
                Then("InvalidCredentialsException이 발생한다") {
                    shouldThrow<InvalidCredentialsException> { service.execute(request) }
                    verify(exactly = 0) { tokenIssuer.issue(any()) }
                }
            }
        }

        Given("비밀번호가 일치하지 않을 때") {
            every { memberRepository.findByEmail(request.email) } returns member
            every { passwordEncoder.matches(request.password, member.password) } returns false

            When("로그인하면") {
                Then("InvalidCredentialsException이 발생한다") {
                    shouldThrow<InvalidCredentialsException> { service.execute(request) }
                    verify(exactly = 0) { tokenIssuer.issue(any()) }
                }
            }
        }
    })

package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProperties
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import java.time.LocalDateTime
import java.util.Optional

class ReissueServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val jwtProvider = mockk<JwtProvider>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val jwtProperties =
            JwtProperties(
                secret = "test-secret-key-for-hororog-must-be-at-least-32-bytes-long",
                accessExpiration = 1_800_000,
                refreshExpiration = 1_209_600_000,
            )
        val service =
            ReissueServiceImpl(memberRepository, jwtProvider, refreshTokenRepository, jwtProperties)

        val refreshToken = "valid-refresh-token"
        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        Given("유효하고 Redis 저장값과 일치하는 refresh 토큰일 때") {
            every { jwtProvider.validateToken(refreshToken) } returns true
            every { jwtProvider.getUserId(refreshToken) } returns 1L
            every { refreshTokenRepository.findById(1L) } returns
                Optional.of(RefreshToken(1L, refreshToken, 1_209_600L))
            every { memberRepository.findById(1L) } returns Optional.of(member)
            every { jwtProvider.createAccessToken(1L, Role.NUTRITIONIST) } returns "new-access"
            every { jwtProvider.createRefreshToken(1L) } returns "new-refresh"
            every { jwtProvider.getExpiration("new-access") } returns LocalDateTime.of(2026, 6, 11, 13, 0)
            every { jwtProvider.getExpiration("new-refresh") } returns LocalDateTime.of(2026, 6, 25, 13, 0)
            val savedSlot = slot<RefreshToken>()
            every { refreshTokenRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("재발급하면") {
                val response = service.execute(refreshToken)

                Then("새 access/refresh를 발급하고 회전된 refresh를 Redis에 저장한다") {
                    response.accessToken shouldBe "new-access"
                    response.refreshToken shouldBe "new-refresh"
                    response.role shouldBe Role.NUTRITIONIST
                    savedSlot.captured.token shouldBe "new-refresh"
                    savedSlot.captured.userId shouldBe 1L
                }
            }
        }

        Given("토큰 자체가 유효하지 않을 때") {
            every { jwtProvider.validateToken(refreshToken) } returns false

            When("재발급하면") {
                Then("InvalidTokenException이 발생한다") {
                    shouldThrow<InvalidTokenException> { service.execute(refreshToken) }
                    verify(exactly = 0) { refreshTokenRepository.save(any()) }
                }
            }
        }

        Given("Redis에 저장된 refresh 토큰과 값이 다를 때") {
            every { jwtProvider.validateToken(refreshToken) } returns true
            every { jwtProvider.getUserId(refreshToken) } returns 1L
            every { refreshTokenRepository.findById(1L) } returns
                Optional.of(RefreshToken(1L, "another-refresh-token", 1_209_600L))

            When("재발급하면") {
                Then("InvalidTokenException이 발생한다") {
                    shouldThrow<InvalidTokenException> { service.execute(refreshToken) }
                    verify(exactly = 0) { refreshTokenRepository.save(any()) }
                }
            }
        }
    })

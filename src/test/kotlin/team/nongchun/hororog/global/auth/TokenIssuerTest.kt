package team.nongchun.hororog.global.auth

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import java.time.LocalDateTime

class TokenIssuerTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val jwtProvider = mockk<JwtProvider>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val jwtProperties =
            JwtProperties(
                secret = "test-secret-key-for-hororog-must-be-at-least-32-bytes-long",
                accessExpiration = 1_800_000,
                refreshExpiration = 1_209_600_000,
            )
        val tokenIssuer = TokenIssuer(jwtProvider, refreshTokenRepository, jwtProperties)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        Given("회원에게 토큰을 발급할 때") {
            every { jwtProvider.createAccessToken(1L, Role.NUTRITIONIST) } returns "access-token"
            every { jwtProvider.createRefreshToken(1L) } returns "refresh-token"
            every { jwtProvider.getExpiration("access-token") } returns LocalDateTime.of(2026, 6, 12, 12, 30)
            every { jwtProvider.getExpiration("refresh-token") } returns LocalDateTime.of(2026, 6, 26, 12, 0)
            val savedSlot = slot<RefreshToken>()
            every { refreshTokenRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

            When("발급하면") {
                val response = tokenIssuer.issue(member)

                Then("access/refresh 토큰과 만료일시, role을 반환한다") {
                    response.accessToken shouldBe "access-token"
                    response.refreshToken shouldBe "refresh-token"
                    response.accessTokenExpiresAt shouldBe LocalDateTime.of(2026, 6, 12, 12, 30)
                    response.refreshTokenExpiresAt shouldBe LocalDateTime.of(2026, 6, 26, 12, 0)
                    response.role shouldBe Role.NUTRITIONIST
                }

                Then("refresh 토큰을 초 단위 TTL과 함께 Redis에 저장한다") {
                    savedSlot.captured.userId shouldBe 1L
                    savedSlot.captured.token shouldBe "refresh-token"
                    savedSlot.captured.ttl shouldBe 1_209_600L
                }
            }
        }
    })

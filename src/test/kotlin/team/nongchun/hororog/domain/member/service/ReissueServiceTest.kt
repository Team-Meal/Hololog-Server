package team.nongchun.hororog.domain.member.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import team.nongchun.hororog.global.auth.TokenIssuer
import team.nongchun.hororog.global.auth.TokenType
import java.time.LocalDateTime
import java.util.Optional

class ReissueServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val memberRepository = mockk<MemberRepository>()
        val jwtProvider = mockk<JwtProvider>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val tokenIssuer = mockk<TokenIssuer>()
        val service =
            ReissueServiceImpl(memberRepository, jwtProvider, refreshTokenRepository, tokenIssuer)

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
        val signinResponse =
            SigninResponse(
                accessToken = "new-access",
                refreshToken = "new-refresh",
                accessTokenExpiresAt = LocalDateTime.of(2026, 6, 11, 13, 0),
                refreshTokenExpiresAt = LocalDateTime.of(2026, 6, 25, 13, 0),
                role = Role.NUTRITIONIST,
            )

        Given("유효하고 Redis 저장값과 일치하는 refresh 토큰일 때") {
            every { jwtProvider.validateToken(refreshToken, TokenType.REFRESH) } returns true
            every { jwtProvider.getUserId(refreshToken) } returns 1L
            every { refreshTokenRepository.findById(1L) } returns
                Optional.of(RefreshToken(1L, refreshToken, 1_209_600L))
            every { memberRepository.findById(1L) } returns Optional.of(member)
            every { tokenIssuer.issue(member) } returns signinResponse

            When("재발급하면") {
                val response = service.execute(refreshToken)

                Then("새 토큰을 발급해 반환한다") {
                    response shouldBe signinResponse
                    verify(exactly = 1) { tokenIssuer.issue(member) }
                }
            }
        }

        Given("refresh 타입이 아니거나 유효하지 않은 토큰일 때") {
            every { jwtProvider.validateToken(refreshToken, TokenType.REFRESH) } returns false

            When("재발급하면") {
                Then("InvalidTokenException이 발생한다") {
                    shouldThrow<InvalidTokenException> { service.execute(refreshToken) }
                    verify(exactly = 0) { tokenIssuer.issue(any()) }
                }
            }
        }

        Given("Redis에 저장된 refresh 토큰과 값이 다를 때") {
            every { jwtProvider.validateToken(refreshToken, TokenType.REFRESH) } returns true
            every { jwtProvider.getUserId(refreshToken) } returns 1L
            every { refreshTokenRepository.findById(1L) } returns
                Optional.of(RefreshToken(1L, "another-refresh-token", 1_209_600L))

            When("재발급하면") {
                Then("InvalidTokenException이 발생한다") {
                    shouldThrow<InvalidTokenException> { service.execute(refreshToken) }
                    verify(exactly = 0) { tokenIssuer.issue(any()) }
                }
            }
        }
    })

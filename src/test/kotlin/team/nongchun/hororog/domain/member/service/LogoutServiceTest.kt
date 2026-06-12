package team.nongchun.hororog.domain.member.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.auth.RefreshTokenRepository

class LogoutServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val authenticationHolder = mockk<AuthenticationHolder>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val service = LogoutServiceImpl(authenticationHolder, refreshTokenRepository)

        Given("인증된 회원이 로그아웃할 때") {
            every { authenticationHolder.getCurrentUserId() } returns 1L
            justRun { refreshTokenRepository.deleteById(1L) }

            When("로그아웃하면") {
                service.execute()

                Then("현재 회원의 refresh 토큰을 Redis에서 삭제한다") {
                    verify(exactly = 1) { refreshTokenRepository.deleteById(1L) }
                }
            }
        }
    })

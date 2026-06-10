package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.auth.RefreshTokenRepository

@Service
@Transactional(readOnly = true)
class LogoutServiceImpl(
    private val authenticationHolder: AuthenticationHolder,
    private val refreshTokenRepository: RefreshTokenRepository,
) : LogoutService {
    override fun execute() {
        val userId = authenticationHolder.getCurrentUserId()
        refreshTokenRepository.deleteById(userId)
    }
}

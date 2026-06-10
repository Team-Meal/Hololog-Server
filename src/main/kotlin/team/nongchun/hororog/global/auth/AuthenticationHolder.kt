package team.nongchun.hororog.global.auth

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import team.nongchun.hororog.global.exception.AuthenticationRequiredException

/**
 * 현재 인증된 요청의 사용자 정보를 SecurityContext에서 꺼내는 전용 컴포넌트.
 *
 * 비즈니스 서비스는 SecurityContext에 직접 접근하지 않고 이 컴포넌트를 통해 userId를 얻는다.
 */
@Component
class AuthenticationHolder {
    fun getCurrentUserId(): Long = getCurrentUser().userId

    fun getCurrentUser(): CustomUserDetails {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw AuthenticationRequiredException()
        return authentication.principal as? CustomUserDetails
            ?: throw AuthenticationRequiredException()
    }
}

package team.nongchun.hororog.global.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)
        if (token != null && jwtProvider.validateToken(token, TokenType.ACCESS)) {
            authenticate(token)
        }
        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        val userDetails =
            try {
                customUserDetailsService.loadUserByUsername(jwtProvider.getUserId(token))
            } catch (e: MemberNotFoundException) {
                // 토큰은 유효하지만 회원이 삭제된 경우 — 인증 없이 통과시켜 인가 단계에서 401로 처리되게 한다.
                logger.info("토큰의 회원이 존재하지 않아 인증을 건너뜁니다.")
                return
            }
        val authentication =
            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (header.startsWith(BEARER_PREFIX)) header.substring(BEARER_PREFIX.length) else null
    }
}

package team.nongchun.hororog.global.auth

import org.springframework.stereotype.Component
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.entity.Member

/**
 * access/refresh 토큰 발급과 refresh 토큰의 Redis 저장(회전)을 한 곳에서 처리한다.
 *
 * 로그인과 재발급에서 동일한 발급 규칙을 공유하기 위한 컴포넌트.
 */
@Component
class TokenIssuer(
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) {
    fun issue(member: Member): SigninResponse {
        val accessToken = jwtProvider.createAccessToken(member.id, member.role)
        val refreshToken = jwtProvider.createRefreshToken(member.id)
        refreshTokenRepository.save(
            RefreshToken(
                userId = member.id,
                token = refreshToken,
                ttl = jwtProperties.refreshExpiration / MILLIS_PER_SECOND,
            ),
        )
        return SigninResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = jwtProvider.getExpiration(accessToken),
            refreshTokenExpiresAt = jwtProvider.getExpiration(refreshToken),
            role = member.role,
        )
    }

    companion object {
        private const val MILLIS_PER_SECOND = 1000L
    }
}

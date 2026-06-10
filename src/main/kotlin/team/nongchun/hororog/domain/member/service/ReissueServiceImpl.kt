package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProperties
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository

@Service
@Transactional(readOnly = true)
class ReissueServiceImpl(
    private val memberRepository: MemberRepository,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : ReissueService {
    override fun execute(refreshToken: String): SigninResponse {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw InvalidTokenException()
        }
        val userId = jwtProvider.getUserId(refreshToken)
        val stored =
            refreshTokenRepository
                .findById(userId)
                .orElseThrow { InvalidTokenException() }
        if (stored.token != refreshToken) {
            throw InvalidTokenException()
        }

        val member =
            memberRepository
                .findById(userId)
                .orElseThrow { MemberNotFoundException() }

        val newAccessToken = jwtProvider.createAccessToken(member.id, member.role)
        val newRefreshToken = jwtProvider.createRefreshToken(member.id)
        refreshTokenRepository.save(
            RefreshToken(
                userId = member.id,
                token = newRefreshToken,
                ttl = jwtProperties.refreshExpiration / MILLIS_PER_SECOND,
            ),
        )

        return SigninResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            accessTokenExpiresAt = jwtProvider.getExpiration(newAccessToken),
            refreshTokenExpiresAt = jwtProvider.getExpiration(newRefreshToken),
            role = member.role,
        )
    }

    companion object {
        private const val MILLIS_PER_SECOND = 1000L
    }
}

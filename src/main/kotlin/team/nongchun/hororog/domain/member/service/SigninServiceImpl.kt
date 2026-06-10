package team.nongchun.hororog.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProperties
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshToken
import team.nongchun.hororog.global.auth.RefreshTokenRepository

@Service
@Transactional(readOnly = true)
class SigninServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : SigninService {
    override fun execute(request: SigninRequest): SigninResponse {
        val member =
            memberRepository.findByEmail(request.email)
                ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(request.password, member.password)) {
            throw InvalidCredentialsException()
        }

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

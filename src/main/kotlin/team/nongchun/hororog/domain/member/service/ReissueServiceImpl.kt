package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.exception.InvalidTokenException
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.JwtProvider
import team.nongchun.hororog.global.auth.RefreshTokenRepository
import team.nongchun.hororog.global.auth.TokenIssuer
import team.nongchun.hororog.global.auth.TokenType

@Service
@Transactional(readOnly = true)
class ReissueServiceImpl(
    private val memberRepository: MemberRepository,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenIssuer: TokenIssuer,
) : ReissueService {
    override fun execute(refreshToken: String): SigninResponse {
        if (!jwtProvider.validateToken(refreshToken, TokenType.REFRESH)) {
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

        return tokenIssuer.issue(member)
    }
}

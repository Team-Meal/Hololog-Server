package team.nongchun.hororog.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.exception.InvalidCredentialsException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.TokenIssuer

@Service
@Transactional(readOnly = true)
class SigninServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenIssuer: TokenIssuer,
) : SigninService {
    override fun execute(request: SigninRequest): SigninResponse {
        val member =
            memberRepository.findByEmail(request.email)
                ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(request.password, member.password)) {
            throw InvalidCredentialsException()
        }
        return tokenIssuer.issue(member)
    }
}

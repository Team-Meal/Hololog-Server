package team.nongchun.hororog.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SignupRequest
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.EmailAlreadyExistsException
import team.nongchun.hororog.domain.member.repository.MemberRepository

@Service
@Transactional
class SignupServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) : SignupService {
    override fun execute(request: SignupRequest) {
        if (memberRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException()
        }
        memberRepository.save(
            Member(
                email = request.email,
                password = requireNotNull(passwordEncoder.encode(request.password)),
                name = request.name,
                schoolName = request.schoolName,
                role = Role.PENDING_NUTRITIONIST,
            ),
        )
    }
}

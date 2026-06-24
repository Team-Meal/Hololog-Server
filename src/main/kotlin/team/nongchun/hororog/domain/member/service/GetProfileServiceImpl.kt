package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.ProfileResponse
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetProfileServiceImpl(
    private val authenticationHolder: AuthenticationHolder,
    private val memberRepository: MemberRepository,
) : GetProfileService {
    override fun execute(): ProfileResponse {
        val userId = authenticationHolder.getCurrentUserId()
        val member =
            memberRepository
                .findById(userId)
                .orElseThrow { MemberNotFoundException() }
        return ProfileResponse.from(member)
    }
}

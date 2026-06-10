package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.CreateSignupRequestRequest
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateSignupRequestServiceImpl(
    private val memberRepository: MemberRepository,
    private val nutritionistSignupRequestRepository: NutritionistSignupRequestRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateSignupRequestService {
    override fun execute(request: CreateSignupRequestRequest): SignupRequestResponse {
        val userId = authenticationHolder.getCurrentUserId()
        val member =
            memberRepository
                .findById(userId)
                .orElseThrow { MemberNotFoundException() }
        val saved =
            nutritionistSignupRequestRepository.save(
                NutritionistSignupRequest(
                    member = member,
                    licenseNumber = request.licenseNumber,
                    status = SignupStatus.PENDING,
                ),
            )
        return SignupRequestResponse.from(saved)
    }
}

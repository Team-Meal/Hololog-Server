package team.nongchun.hororog.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.exception.SignupRequestAlreadyProcessedException
import team.nongchun.hororog.domain.member.exception.SignupRequestNotFoundException
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository

@Service
@Transactional
class RejectSignupRequestServiceImpl(
    private val nutritionistSignupRequestRepository: NutritionistSignupRequestRepository,
) : RejectSignupRequestService {
    override fun execute(requestId: Long): SignupRequestResponse {
        val signupRequest =
            nutritionistSignupRequestRepository
                .findById(requestId)
                .orElseThrow { SignupRequestNotFoundException() }
        if (signupRequest.status != SignupStatus.PENDING) {
            throw SignupRequestAlreadyProcessedException()
        }
        signupRequest.status = SignupStatus.REJECTED
        return SignupRequestResponse.from(signupRequest)
    }
}

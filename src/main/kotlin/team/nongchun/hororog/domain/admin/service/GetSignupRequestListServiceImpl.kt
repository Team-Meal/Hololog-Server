package team.nongchun.hororog.domain.admin.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.admin.dto.SignupRequestItem
import team.nongchun.hororog.domain.member.entity.SignupStatus
import team.nongchun.hororog.domain.member.repository.NutritionistSignupRequestRepository

@Service
@Transactional(readOnly = true)
class GetSignupRequestListServiceImpl(
    private val nutritionistSignupRequestRepository: NutritionistSignupRequestRepository,
) : GetSignupRequestListService {
    override fun execute(pageable: Pageable): Page<SignupRequestItem> =
        nutritionistSignupRequestRepository
            .findByStatus(SignupStatus.PENDING, pageable)
            .map { SignupRequestItem.from(it) }
}

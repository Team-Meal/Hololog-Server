package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.exception.IngredientPlanNotFoundException
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateIngredientPlanServiceImpl(
    private val ingredientPlanRepository: IngredientPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateIngredientPlanService {
    override fun execute(
        planId: Long,
        request: UpdateIngredientPlanRequest,
    ): IngredientPlanUpdateResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val plan =
            ingredientPlanRepository.findByIdAndMemberSchoolName(planId, schoolName)
                ?: throw IngredientPlanNotFoundException()

        request.title?.let { plan.title = it }
        request.startDate?.let { plan.startDate = it }
        request.endDate?.let { plan.endDate = it }
        request.memo?.let { plan.memo = it }

        return IngredientPlanUpdateResponse.from(ingredientPlanRepository.saveAndFlush(plan))
    }
}

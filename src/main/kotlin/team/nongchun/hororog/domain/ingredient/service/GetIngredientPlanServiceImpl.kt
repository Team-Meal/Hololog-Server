package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanResponse
import team.nongchun.hororog.domain.ingredient.exception.IngredientPlanNotFoundException
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetIngredientPlanServiceImpl(
    private val ingredientPlanRepository: IngredientPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetIngredientPlanService {
    override fun execute(planId: Long): IngredientPlanResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val plan =
            ingredientPlanRepository.findByIdAndMemberSchoolName(planId, schoolName)
                ?: throw IngredientPlanNotFoundException()
        return IngredientPlanResponse.from(plan)
    }
}

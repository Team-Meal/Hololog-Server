package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanResponse
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetIngredientPlanListServiceImpl(
    private val ingredientPlanRepository: IngredientPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetIngredientPlanListService {
    override fun execute(): List<IngredientPlanResponse> {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        return ingredientPlanRepository
            .findAllByMemberSchoolName(schoolName)
            .map(IngredientPlanResponse::from)
    }
}

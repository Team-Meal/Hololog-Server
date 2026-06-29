package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateIngredientPlanServiceImpl(
    private val ingredientPlanRepository: IngredientPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateIngredientPlanService {
    override fun execute(request: CreateIngredientPlanRequest) {
        IngredientPlanValidator.validate(request.startDate, request.endDate)
        val member =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
        ingredientPlanRepository.save(
            IngredientPlan(
                member = member,
                title = request.title,
                startDate = request.startDate,
                endDate = request.endDate,
                memo = request.memo,
            ),
        )
    }
}

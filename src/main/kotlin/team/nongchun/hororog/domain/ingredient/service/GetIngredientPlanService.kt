package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanResponse

interface GetIngredientPlanService {
    fun execute(planId: Long): IngredientPlanResponse
}

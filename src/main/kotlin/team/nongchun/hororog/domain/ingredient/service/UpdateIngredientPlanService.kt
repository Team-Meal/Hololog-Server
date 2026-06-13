package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientPlanRequest

interface UpdateIngredientPlanService {
    fun execute(
        planId: Long,
        request: UpdateIngredientPlanRequest,
    ): IngredientPlanUpdateResponse
}

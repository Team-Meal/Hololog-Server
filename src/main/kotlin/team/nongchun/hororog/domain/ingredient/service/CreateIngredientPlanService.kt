package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientPlanRequest

interface CreateIngredientPlanService {
    fun execute(request: CreateIngredientPlanRequest)
}

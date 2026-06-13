package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientPlanResponse

interface GetIngredientPlanListService {
    fun execute(): List<IngredientPlanResponse>
}

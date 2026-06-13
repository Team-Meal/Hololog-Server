package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientRequest

interface UpdateIngredientService {
    fun execute(
        ingredientId: Long,
        request: UpdateIngredientRequest,
    ): IngredientUpdateResponse
}

package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientResponse

interface GetIngredientService {
    fun execute(ingredientId: Long): IngredientResponse
}

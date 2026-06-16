package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientListResponse

interface GetIngredientListService {
    fun execute(): List<IngredientListResponse>
}

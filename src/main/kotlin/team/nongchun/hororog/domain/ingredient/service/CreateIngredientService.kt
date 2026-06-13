package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientRequest

interface CreateIngredientService {
    fun execute(request: CreateIngredientRequest)
}

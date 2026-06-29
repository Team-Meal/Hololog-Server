package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.dto.IngredientPriceResponse

interface KamisPriceService {
    fun getPrices(): List<IngredientPriceResponse>

    fun syncPrices(): List<IngredientPriceResponse>

    fun getPriceAlerts(): List<IngredientPriceResponse>
}

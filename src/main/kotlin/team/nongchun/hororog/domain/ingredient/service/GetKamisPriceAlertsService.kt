package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.ingredient.dto.IngredientPriceResponse

@Service
class GetKamisPriceAlertsService(
    private val getKamisPriceService: GetKamisPriceService,
) {
    fun execute(): List<IngredientPriceResponse> = getKamisPriceService.execute().filter { it.isPriceSurge }
}

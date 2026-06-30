package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.ingredient.cache.KamisPriceCacheRepository
import team.nongchun.hororog.domain.ingredient.dto.IngredientPriceResponse

@Service
class GetKamisPriceService(
    private val kamisPriceCacheRepository: KamisPriceCacheRepository,
    private val syncKamisPriceService: SyncKamisPriceService,
) {
    fun execute(): List<IngredientPriceResponse> {
        val cached = kamisPriceCacheRepository.findAll().toList()
        if (cached.isNotEmpty()) {
            return cached.map { IngredientPriceResponse.from(it) }
        }
        return syncKamisPriceService.execute()
    }
}

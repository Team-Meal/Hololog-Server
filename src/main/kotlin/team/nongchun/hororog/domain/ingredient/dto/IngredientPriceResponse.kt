package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.cache.KamisPriceCache
import kotlin.math.roundToInt

data class IngredientPriceResponse(
    val itemName: String,
    val pricePerKg: Int,
    val unit: String,
    val baseDate: String,
    val previousPricePerKg: Int?,
    val priceChangeRate: Double?,
    val isPriceSurge: Boolean,
    val alternatives: List<String>,
) {
    companion object {
        private const val SURGE_THRESHOLD = 18.0

        private val ALTERNATIVES_MAP =
            mapOf(
                "토마토" to listOf("오이", "양배추"),
                "수박" to listOf("참외", "멜론"),
                "감자" to listOf("고구마", "옥수수"),
                "양파" to listOf("대파", "마늘"),
                "배추" to listOf("양배추", "상추"),
                "오이" to listOf("호박", "양배추"),
                "당근" to listOf("무", "우엉"),
                "무" to listOf("당근", "감자"),
                "고구마" to listOf("감자", "옥수수"),
                "파" to listOf("양파", "부추"),
            )

        fun from(cache: KamisPriceCache): IngredientPriceResponse {
            val changeRate =
                if (cache.previousPricePerKg != null && cache.previousPricePerKg > 0) {
                    (cache.pricePerKg - cache.previousPricePerKg).toDouble() / cache.previousPricePerKg * 100
                } else {
                    null
                }
            val isSurge = changeRate != null && changeRate >= SURGE_THRESHOLD
            return IngredientPriceResponse(
                itemName = cache.itemName,
                pricePerKg = cache.pricePerKg,
                unit = cache.unit,
                baseDate = cache.baseDate,
                previousPricePerKg = cache.previousPricePerKg,
                priceChangeRate = changeRate?.let { (it * 10).roundToInt() / 10.0 },
                isPriceSurge = isSurge,
                alternatives = if (isSurge) ALTERNATIVES_MAP[cache.itemName] ?: emptyList() else emptyList(),
            )
        }
    }
}

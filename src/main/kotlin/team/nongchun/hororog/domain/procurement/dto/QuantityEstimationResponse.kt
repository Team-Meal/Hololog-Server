package team.nongchun.hororog.domain.procurement.dto

data class QuantityEstimationResponse(
    val totalPrice: Int?,
    val totalCalorie: Int?,
    val ingredients: List<IngredientEstimation>,
) {
    data class IngredientEstimation(
        val name: String,
        val quantity: Int,
        val unit: String,
        val price: Int?,
        val calorie: Int?,
    )
}

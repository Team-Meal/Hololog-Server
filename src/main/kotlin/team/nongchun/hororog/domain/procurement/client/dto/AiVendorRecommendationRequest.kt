package team.nongchun.hororog.domain.procurement.client.dto

// TODO: AI 서버 스펙 확정 후 필드명 조정
data class AiVendorRecommendationRequest(
    val ingredients: List<IngredientInfo>,
) {
    data class IngredientInfo(
        val ingredientId: Long,
        val name: String,
        val category: String,
        val quantity: Int,
        val unit: String,
    )
}

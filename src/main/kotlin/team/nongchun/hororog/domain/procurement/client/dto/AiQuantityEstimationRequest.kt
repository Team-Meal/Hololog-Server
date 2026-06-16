package team.nongchun.hororog.domain.procurement.client.dto

// TODO: AI 서버 스펙 확정 후 필드명 조정
data class AiQuantityEstimationRequest(
    val ingredients: List<IngredientItem>,
) {
    data class IngredientItem(
        val name: String,
        val quantity: Int,
        val unit: String,
    )
}

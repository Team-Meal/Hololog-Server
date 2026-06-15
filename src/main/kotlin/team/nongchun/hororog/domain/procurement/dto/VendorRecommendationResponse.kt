package team.nongchun.hororog.domain.procurement.dto

data class VendorRecommendationResponse(
    val recommendations: List<IngredientRecommendation>,
) {
    data class IngredientRecommendation(
        val ingredientId: Long,
        val ingredientName: String,
        val category: String,
        val suppliers: List<SupplierInfo>,
    )

    data class SupplierInfo(
        val supplierId: String,
        val name: String,
        val contact: String?,
        val address: String?,
        val price: Int?,
        val unit: String?,
        val description: String?,
        val source: String,
    )
}

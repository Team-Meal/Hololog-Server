package team.nongchun.hororog.domain.procurement.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class QuantityEstimationRequest(
    @field:NotEmpty
    @field:Valid
    val ingredients: List<IngredientItem>,
) {
    data class IngredientItem(
        @field:NotBlank
        val name: String,
        @field:Positive
        val quantity: Int,
        @field:NotBlank
        val unit: String,
    )
}

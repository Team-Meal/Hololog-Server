package team.nongchun.hororog.domain.order.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class AddOrderPlanItemRequest(
    val ingredientId: Long,
    @field:NotBlank
    @field:Size(max = 100)
    val menuName: String,
    @field:Positive
    val perPersonUsage: Double,
    @field:NotBlank
    val unit: String,
    val supplierName: String? = null,
    val unitPrice: Double? = null,
)

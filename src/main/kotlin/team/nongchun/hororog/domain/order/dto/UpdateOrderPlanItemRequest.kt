package team.nongchun.hororog.domain.order.dto

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class UpdateOrderPlanItemRequest(
    @field:Size(max = 100)
    val menuName: String? = null,
    @field:Positive
    val perPersonUsage: Double? = null,
    val unit: String? = null,
    val supplierName: String? = null,
    val unitPrice: Double? = null,
)

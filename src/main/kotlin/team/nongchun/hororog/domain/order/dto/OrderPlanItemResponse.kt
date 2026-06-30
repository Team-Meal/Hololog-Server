package team.nongchun.hororog.domain.order.dto

import team.nongchun.hororog.domain.order.entity.OrderPlanItem
import team.nongchun.hororog.global.common.QuantityUnit

data class OrderPlanItemResponse(
    val id: Long,
    val menuName: String,
    val ingredientName: String,
    val unit: QuantityUnit,
    val requiredQuantity: Double,
    val currentStock: Double,
    val shortageQuantity: Double,
    val orderQuantity: Double,
    val supplierName: String?,
    val unitPrice: Double?,
    val estimatedCost: Double?,
    val basis: String?,
) {
    companion object {
        fun from(entity: OrderPlanItem) =
            OrderPlanItemResponse(
                id = entity.id,
                menuName = entity.menuName,
                ingredientName = entity.ingredient.name,
                unit = entity.unit,
                requiredQuantity = entity.requiredQuantity,
                currentStock = entity.currentStock,
                shortageQuantity = entity.shortageQuantity,
                orderQuantity = entity.orderQuantity,
                supplierName = entity.supplierName,
                unitPrice = entity.unitPrice,
                estimatedCost = entity.estimatedCost,
                basis = entity.basis,
            )
    }
}

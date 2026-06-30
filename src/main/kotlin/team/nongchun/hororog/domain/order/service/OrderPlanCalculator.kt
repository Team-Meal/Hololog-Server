package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.entity.OrderPlanItem

object OrderPlanCalculator {
    fun recalculate(
        item: OrderPlanItem,
        studentCount: Int,
    ) {
        item.requiredQuantity = item.perPersonUsage * studentCount
        item.shortageQuantity = maxOf(item.requiredQuantity - item.currentStock, 0.0)
        item.orderQuantity = item.shortageQuantity * 1.05
        item.estimatedCost = item.unitPrice?.let { item.orderQuantity * it }
        item.basis = "${item.menuName} / 1인 ${item.perPersonUsage}${item.unit.name} × ${studentCount}명"
    }

    fun recalculateStock(item: OrderPlanItem) {
        item.shortageQuantity = maxOf(item.requiredQuantity - item.currentStock, 0.0)
        item.orderQuantity = item.shortageQuantity * 1.05
        item.estimatedCost = item.unitPrice?.let { item.orderQuantity * it }
    }
}

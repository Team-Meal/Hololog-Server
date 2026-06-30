package team.nongchun.hororog.domain.order.dto

import team.nongchun.hororog.domain.order.entity.OrderPlan
import java.time.LocalDate
import java.time.LocalDateTime

data class OrderPlanDetailResponse(
    val id: Long,
    val title: String,
    val planDate: LocalDate,
    val studentCount: Int,
    val memo: String?,
    val totalEstimatedCost: Double?,
    val items: List<OrderPlanItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: OrderPlan,
            items: List<OrderPlanItemResponse>,
        ) = OrderPlanDetailResponse(
            id = entity.id,
            title = entity.title,
            planDate = entity.planDate,
            studentCount = entity.studentCount,
            memo = entity.memo,
            totalEstimatedCost =
                items.mapNotNull { it.estimatedCost }.sum().takeIf {
                    items.isNotEmpty() &&
                        items.all { i -> i.estimatedCost != null }
                },
            items = items,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}

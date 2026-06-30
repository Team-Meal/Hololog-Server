package team.nongchun.hororog.domain.order.dto

import team.nongchun.hororog.domain.order.entity.OrderPlan
import java.time.LocalDate
import java.time.LocalDateTime

data class OrderPlanListResponse(
    val id: Long,
    val title: String,
    val planDate: LocalDate,
    val studentCount: Int,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: OrderPlan) =
            OrderPlanListResponse(
                id = entity.id,
                title = entity.title,
                planDate = entity.planDate,
                studentCount = entity.studentCount,
                createdAt = entity.createdAt,
            )
    }
}

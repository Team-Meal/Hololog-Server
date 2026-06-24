package team.nongchun.hororog.domain.budget.dto

import team.nongchun.hororog.domain.budget.entity.Budget
import java.time.LocalDate
import java.time.LocalDateTime

data class BudgetListResponse(
    val id: Long,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(budget: Budget) =
            BudgetListResponse(
                id = budget.id,
                title = budget.name,
                startDate = budget.startDate,
                endDate = budget.endDate,
                createdAt = budget.createdAt,
                updatedAt = budget.updatedAt,
            )
    }
}

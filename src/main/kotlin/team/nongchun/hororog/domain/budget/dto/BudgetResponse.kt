package team.nongchun.hororog.domain.budget.dto

import team.nongchun.hororog.domain.budget.entity.Budget
import java.time.LocalDate
import java.time.LocalDateTime

data class BudgetResponse(
    val id: Long,
    val title: String,
    val totalAmount: Long,
    val usedAmount: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(budget: Budget) =
            BudgetResponse(
                id = budget.id,
                title = budget.name,
                totalAmount = budget.totalBudget,
                usedAmount = budget.usedBudget,
                startDate = budget.startDate,
                endDate = budget.endDate,
                createdAt = budget.createdAt,
                updatedAt = budget.updatedAt,
            )
    }
}

package team.nongchun.hororog.domain.budget.dto

data class BudgetMonthlySummaryResponse(
    val month: String,
    val totalBudget: Long,
    val usedBudget: Long,
    val remaining: Long,
    val currency: String = "KRW",
)

package team.nongchun.hororog.domain.budget.dto

data class BudgetMonthlySummaryResponse(
    val month: String,
    val totalBudget: Int,
    val usedBudget: Int,
    val remaining: Int,
    val currency: String = "KRW",
)

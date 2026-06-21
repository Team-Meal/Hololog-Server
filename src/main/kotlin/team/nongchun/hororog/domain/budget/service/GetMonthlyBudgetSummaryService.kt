package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.BudgetMonthlySummaryResponse

interface GetMonthlyBudgetSummaryService {
    fun execute(month: String): BudgetMonthlySummaryResponse
}

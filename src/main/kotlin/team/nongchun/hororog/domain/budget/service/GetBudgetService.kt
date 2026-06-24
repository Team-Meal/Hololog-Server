package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.BudgetResponse

interface GetBudgetService {
    fun execute(budgetId: Long): BudgetResponse
}

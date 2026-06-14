package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.BudgetUpdateResponse
import team.nongchun.hororog.domain.budget.dto.UpdateBudgetRequest

interface UpdateBudgetService {
    fun execute(
        budgetId: Long,
        request: UpdateBudgetRequest,
    ): BudgetUpdateResponse
}

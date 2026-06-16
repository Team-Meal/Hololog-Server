package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.UpdateBudgetRequest
import team.nongchun.hororog.domain.budget.dto.UpdateBudgetResponse

interface UpdateBudgetService {
    fun execute(
        budgetId: Long,
        request: UpdateBudgetRequest,
    ): UpdateBudgetResponse
}

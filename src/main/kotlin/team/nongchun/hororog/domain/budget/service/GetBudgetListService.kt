package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.BudgetListResponse

interface GetBudgetListService {
    fun execute(): List<BudgetListResponse>
}

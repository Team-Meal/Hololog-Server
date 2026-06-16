package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.dto.CreateBudgetRequest

interface CreateBudgetService {
    fun execute(request: CreateBudgetRequest)
}

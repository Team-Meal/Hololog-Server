package team.nongchun.hororog.domain.budget.dto

import team.nongchun.hororog.domain.budget.entity.Budget

data class BudgetUpdateResponse(
    val id: Long,
) {
    companion object {
        fun from(budget: Budget) = BudgetUpdateResponse(id = budget.id)
    }
}

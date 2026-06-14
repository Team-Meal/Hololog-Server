package team.nongchun.hororog.domain.budget.dto

import team.nongchun.hororog.domain.budget.entity.Budget

data class UpdateBudgetResponse(
    val id: Long,
) {
    companion object {
        fun from(budget: Budget) = UpdateBudgetResponse(id = budget.id)
    }
}

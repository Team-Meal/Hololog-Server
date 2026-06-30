package team.nongchun.hororog.domain.budget.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateBudgetRequest(
    @field:Size(max = 100)
    val title: String? = null,
    @field:Min(0)
    val totalAmount: Long? = null,
    @field:Min(0)
    val usedAmount: Long? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)

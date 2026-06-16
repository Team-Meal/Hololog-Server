package team.nongchun.hororog.domain.budget.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateBudgetRequest(
    @field:Size(max = 100)
    val title: String? = null,
    @field:Min(0)
    val totalAmount: Int? = null,
    @field:Min(0)
    val usedAmount: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)

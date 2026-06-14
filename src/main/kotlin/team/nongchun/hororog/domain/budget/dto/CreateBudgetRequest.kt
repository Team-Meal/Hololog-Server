package team.nongchun.hororog.domain.budget.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateBudgetRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    @field:Min(0)
    val totalAmount: Int,
    @field:Min(0)
    val usedAmount: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

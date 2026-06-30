package team.nongchun.hororog.domain.order.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateOrderPlanRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    val planDate: LocalDate,
    @field:Min(1)
    val studentCount: Int,
    val memo: String? = null,
)

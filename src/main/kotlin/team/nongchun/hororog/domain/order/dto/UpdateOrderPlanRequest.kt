package team.nongchun.hororog.domain.order.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateOrderPlanRequest(
    @field:Size(max = 100)
    val title: String? = null,
    val planDate: LocalDate? = null,
    @field:Min(1)
    val studentCount: Int? = null,
    val memo: String? = null,
)

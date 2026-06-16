package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UpdateIngredientPlanRequest(
    @field:Size(max = 100)
    val title: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val memo: String? = null,
)

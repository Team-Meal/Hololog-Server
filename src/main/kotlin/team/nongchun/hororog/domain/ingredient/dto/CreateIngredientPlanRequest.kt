package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateIngredientPlanRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val memo: String? = null,
)

package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateIngredientPlanRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val memo: String? = null,
)

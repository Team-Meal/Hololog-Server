package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateIngredientPlanRequest(
    @field:Size(max = 100)
    val title: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val memo: String? = null,
)

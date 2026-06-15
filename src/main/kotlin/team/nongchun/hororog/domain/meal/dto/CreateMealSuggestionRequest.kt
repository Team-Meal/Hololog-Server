package team.nongchun.hororog.domain.meal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateMealSuggestionRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    val content: String?,
)

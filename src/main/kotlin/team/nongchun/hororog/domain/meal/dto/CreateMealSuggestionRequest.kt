package team.nongchun.hororog.domain.meal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus

data class CreateMealSuggestionRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    val content: String?,
    @field:NotNull
    val mealSuggestionStatus: SuggestionStatus,
)

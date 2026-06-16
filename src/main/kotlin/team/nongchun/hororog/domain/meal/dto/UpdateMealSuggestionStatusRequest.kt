package team.nongchun.hororog.domain.meal.dto

import jakarta.validation.constraints.NotNull
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus

data class UpdateMealSuggestionStatusRequest(
    @field:NotNull
    val mealSuggestionStatus: SuggestionStatus,
)

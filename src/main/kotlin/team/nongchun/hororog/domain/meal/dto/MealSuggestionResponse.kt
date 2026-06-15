package team.nongchun.hororog.domain.meal.dto

import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import java.time.LocalDateTime

data class MealSuggestionResponse(
    val id: Long,
    val title: String,
    val content: String?,
    val mealSuggestionStatus: SuggestionStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(mealSuggestion: MealSuggestion) =
            MealSuggestionResponse(
                id = mealSuggestion.id,
                title = mealSuggestion.title,
                content = mealSuggestion.content,
                mealSuggestionStatus = mealSuggestion.status,
                createdAt = mealSuggestion.createdAt,
                updatedAt = mealSuggestion.updatedAt,
            )
    }
}

package team.nongchun.hororog.domain.meal.dto

import team.nongchun.hororog.domain.meal.entity.MealAiGeneration
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus

data class MealAiGenerationResponse(
    val id: Long,
    val status: MealAiGenerationStatus,
) {
    companion object {
        fun from(entity: MealAiGeneration) =
            MealAiGenerationResponse(
                id = entity.id,
                status = entity.status,
            )
    }
}

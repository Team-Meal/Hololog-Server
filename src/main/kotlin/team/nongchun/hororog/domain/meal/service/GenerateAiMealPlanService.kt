package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest
import team.nongchun.hororog.domain.meal.dto.MealAiGenerationResponse

interface GenerateAiMealPlanService {
    fun execute(
        authorization: String,
        request: GenerateAiMealPlanRequest,
    ): MealAiGenerationResponse
}

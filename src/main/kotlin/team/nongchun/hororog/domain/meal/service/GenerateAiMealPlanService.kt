package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.AiMealPlanResponse
import team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest

interface GenerateAiMealPlanService {
    fun execute(
        authorization: String,
        request: GenerateAiMealPlanRequest,
    ): AiMealPlanResponse
}

package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.MealAiGenerationResponse

interface GetAiMealGenerationService {
    fun execute(id: Long): MealAiGenerationResponse
}

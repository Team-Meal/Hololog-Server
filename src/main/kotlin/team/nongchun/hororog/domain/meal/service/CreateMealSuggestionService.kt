package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.CreateMealSuggestionRequest

interface CreateMealSuggestionService {
    fun execute(request: CreateMealSuggestionRequest)
}

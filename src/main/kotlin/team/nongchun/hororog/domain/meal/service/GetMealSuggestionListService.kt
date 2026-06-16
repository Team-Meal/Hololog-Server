package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.MealSuggestionResponse

interface GetMealSuggestionListService {
    fun execute(): List<MealSuggestionResponse>
}

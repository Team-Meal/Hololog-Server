package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.UpdateMealSuggestionStatusRequest

interface UpdateMealSuggestionStatusService {
    fun execute(
        suggestionId: Long,
        request: UpdateMealSuggestionStatusRequest,
    )
}

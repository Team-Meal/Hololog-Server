package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.CreateAiMealRequest

interface CreateAiMealService {
    fun execute(request: CreateAiMealRequest): Long
}

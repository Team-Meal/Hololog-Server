package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest

interface CreateAiMealSlotService {
    fun execute(request: CreateAiMealSlotRequest): Long
}

package team.nongchun.hororog.domain.meal.service

import team.nongchun.hororog.domain.meal.dto.TodayMealResponse
import team.nongchun.hororog.domain.meal.entity.MealType

interface GetTodayMealService {
    fun execute(mealType: MealType): TodayMealResponse
}

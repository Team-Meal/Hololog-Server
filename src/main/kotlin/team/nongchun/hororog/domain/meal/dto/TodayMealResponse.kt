package team.nongchun.hororog.domain.meal.dto

import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealType
import java.time.LocalDate

data class TodayMealResponse(
    val mealDate: LocalDate,
    val mealType: MealType,
    val menuNames: List<String>,
    val calorie: String?,
    val nutritionInfo: String?,
    val originInfo: String?,
) {
    companion object {
        fun of(
            mealDate: LocalDate,
            mealType: MealType,
            meals: List<Meal>,
        ) = TodayMealResponse(
            mealDate = mealDate,
            mealType = mealType,
            menuNames = meals.map { it.name },
            calorie =
                meals
                    .mapNotNull { it.totalCalories }
                    .takeIf { it.isNotEmpty() }
                    ?.sum()
                    ?.toString(),
            nutritionInfo =
                meals
                    .mapNotNull { it.nutritionInfo }
                    .distinct()
                    .joinToString("\n")
                    .ifBlank { null },
            originInfo =
                meals
                    .mapNotNull { it.originInfo }
                    .distinct()
                    .joinToString("\n")
                    .ifBlank { null },
        )
    }
}

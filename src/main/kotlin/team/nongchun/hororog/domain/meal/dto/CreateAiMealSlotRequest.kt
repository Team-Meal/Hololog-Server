package team.nongchun.hororog.domain.meal.dto

import com.fasterxml.jackson.annotation.JsonProperty
import team.nongchun.hororog.domain.meal.entity.MealType
import java.time.LocalDate

data class CreateAiMealSlotRequest(
    val date: LocalDate,
    @JsonProperty("meal_type")
    val mealType: MealType,
    @JsonProperty("school_id")
    val schoolId: Long,
)

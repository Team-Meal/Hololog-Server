package team.nongchun.hororog.domain.meal.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateAiMealRequest(
    @JsonProperty("diet_id")
    val dietId: Long,
    @JsonProperty("menu_name")
    val menuName: String,
    val kcal: Double,
    val protein: Double,
    val fat: Double,
    val sodium: Double,
)

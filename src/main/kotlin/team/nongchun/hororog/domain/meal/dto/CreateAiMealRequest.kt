package team.nongchun.hororog.domain.meal.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank

data class CreateAiMealRequest(
    @JsonProperty("diet_id")
    val dietId: Long,
    @JsonProperty("menu_name")
    @field:NotBlank
    val menuName: String,
    @field:DecimalMin("0.0")
    val kcal: Double,
    @field:DecimalMin("0.0")
    val protein: Double,
    @field:DecimalMin("0.0")
    val fat: Double,
    @field:DecimalMin("0.0")
    val sodium: Double,
)

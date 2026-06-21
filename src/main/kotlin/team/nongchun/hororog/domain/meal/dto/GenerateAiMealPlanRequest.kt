package team.nongchun.hororog.domain.meal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class GenerateAiMealPlanRequest(
    @field:NotBlank
    @field:Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "month는 YYYY-MM 형식이어야 합니다")
    val month: String,
    val holidays: List<LocalDate> = emptyList(),
)

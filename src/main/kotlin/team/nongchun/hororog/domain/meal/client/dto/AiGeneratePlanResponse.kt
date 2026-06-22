package team.nongchun.hororog.domain.meal.client.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AiGeneratePlanResponse(
    val month: String,
    @JsonProperty("total_meals")
    val totalMeals: Int,
    @JsonProperty("validation_errors")
    val validationErrors: List<Map<String, Any?>> = emptyList(),
    @JsonProperty("budget_info")
    val budgetInfo: Map<String, Any?> = emptyMap(),
    val error: String? = null,
)

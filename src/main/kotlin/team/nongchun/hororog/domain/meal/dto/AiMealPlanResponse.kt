package team.nongchun.hororog.domain.meal.dto

import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanResponse

data class AiMealPlanResponse(
    val month: String,
    val totalMeals: Int,
    val validationErrors: List<Map<String, Any?>>,
    val budgetInfo: Map<String, Any?>,
    val error: String?,
) {
    companion object {
        fun from(response: AiGeneratePlanResponse) =
            AiMealPlanResponse(
                month = response.month,
                totalMeals = response.totalMeals,
                validationErrors = response.validationErrors,
                budgetInfo = response.budgetInfo,
                error = response.error,
            )
    }
}

package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan
import java.time.LocalDate
import java.time.LocalDateTime

data class IngredientPlanResponse(
    val ingredientPlanId: Long,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val memo: String?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: IngredientPlan) =
            IngredientPlanResponse(
                ingredientPlanId = entity.id,
                title = entity.title,
                startDate = entity.startDate,
                endDate = entity.endDate,
                memo = entity.memo,
                createdAt = entity.createdAt,
            )
    }
}

package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan
import java.time.LocalDateTime

data class IngredientPlanUpdateResponse(
    val ingredientPlanId: Long,
    val title: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val memo: String?,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: IngredientPlan) =
            IngredientPlanUpdateResponse(
                ingredientPlanId = entity.id,
                title = entity.title,
                startDate = entity.startDate,
                endDate = entity.endDate,
                memo = entity.memo,
                updatedAt = entity.updatedAt,
            )
    }
}

package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import java.time.LocalDateTime

data class IngredientUpdateResponse(
    val ingredientId: Long,
    val name: String,
    val quantity: Int,
    val unit: String,
    val expirationDate: LocalDateTime,
    val category: String,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Ingredient) =
            IngredientUpdateResponse(
                ingredientId = entity.id,
                name = entity.name,
                quantity = entity.quantity,
                unit = entity.unit.name,
                expirationDate = entity.expirationDate,
                category = entity.category,
                updatedAt = entity.updatedAt,
            )
    }
}

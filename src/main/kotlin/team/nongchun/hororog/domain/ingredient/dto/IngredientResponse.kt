package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import java.time.LocalDateTime

data class IngredientResponse(
    val ingredientId: Long,
    val name: String,
    val quantity: Int,
    val unit: String,
    val expirationDate: LocalDateTime,
    val category: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Ingredient) =
            IngredientResponse(
                ingredientId = entity.id,
                name = entity.name,
                quantity = entity.quantity,
                unit = entity.unit.name,
                expirationDate = entity.expirationDate,
                category = entity.category,
                createdAt = entity.createdAt,
            )
    }
}

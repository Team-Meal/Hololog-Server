package team.nongchun.hororog.domain.ingredient.dto

import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import java.time.LocalDateTime

data class IngredientListResponse(
    val ingredientId: Long,
    val name: String,
    val quantity: Int,
    val unit: String,
    val expirationDate: LocalDateTime,
    val category: String,
    val origin: String?,
    val supplier: String?,
) {
    companion object {
        fun from(entity: Ingredient) =
            IngredientListResponse(
                ingredientId = entity.id,
                name = entity.name,
                quantity = entity.quantity,
                unit = entity.unit.name,
                expirationDate = entity.expirationDate,
                category = entity.category,
                origin = entity.origin,
                supplier = entity.supplier,
            )
    }
}

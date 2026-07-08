package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UpdateIngredientRequest(
    @field:Size(max = 100)
    val name: String? = null,
    @field:Positive
    val quantity: Int? = null,
    val unit: String? = null,
    val expirationDate: LocalDateTime? = null,
    @field:Size(max = 50)
    val category: String? = null,
    @field:Size(max = 100)
    val origin: String? = null,
    @field:Size(max = 100)
    val supplier: String? = null,
)

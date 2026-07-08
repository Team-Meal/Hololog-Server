package team.nongchun.hororog.domain.ingredient.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateIngredientRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,
    @field:Positive
    val quantity: Int,
    @field:NotBlank
    val unit: String,
    val expirationDate: LocalDateTime,
    @field:NotBlank
    @field:Size(max = 50)
    val category: String,
    @field:Size(max = 100)
    val origin: String? = null,
    @field:Size(max = 100)
    val supplier: String? = null,
)

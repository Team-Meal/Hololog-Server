package team.nongchun.hororog.domain.diet.dto

import team.nongchun.hororog.domain.diet.entity.Diet
import java.time.LocalDate
import java.time.LocalDateTime

data class DietListResponse(
    val id: Long,
    val name: String,
    val dietDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(diet: Diet) =
            DietListResponse(
                id = diet.id,
                name = diet.name,
                dietDate = diet.dietDate,
                createdAt = diet.createdAt,
                updatedAt = diet.updatedAt,
            )
    }
}

package team.nongchun.hororog.domain.diet.dto

import team.nongchun.hororog.domain.leftover.entity.Leftover
import team.nongchun.hororog.global.common.QuantityUnit
import java.time.LocalDate
import java.time.LocalDateTime

data class DietLeftoverResponse(
    val id: Long,
    val name: String,
    val dietDate: LocalDate,
    val amount: Int,
    val unit: QuantityUnit,
    val memo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(leftover: Leftover): DietLeftoverResponse {
            val diet = requireNotNull(leftover.diet)
            return DietLeftoverResponse(
                id = leftover.id,
                name = diet.name,
                dietDate = diet.dietDate,
                amount = leftover.amount,
                unit = leftover.unit,
                memo = leftover.memo,
                createdAt = leftover.createdAt,
                updatedAt = leftover.updatedAt,
            )
        }
    }
}

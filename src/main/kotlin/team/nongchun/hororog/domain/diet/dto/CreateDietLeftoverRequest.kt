package team.nongchun.hororog.domain.diet.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import team.nongchun.hororog.global.common.QuantityUnit

data class CreateDietLeftoverRequest(
    @field:NotNull
    @field:Positive
    val amount: Int,
    @field:NotNull
    val unit: QuantityUnit,
    val memo: String? = null,
)

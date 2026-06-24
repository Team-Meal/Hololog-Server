package team.nongchun.hororog.domain.diet.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateDietRequest(
    @field:NotBlank
    val name: String,
    val description: String? = null,
    @field:NotNull
    val dietDate: LocalDate,
)

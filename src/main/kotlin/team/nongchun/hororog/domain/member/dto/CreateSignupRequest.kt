package team.nongchun.hororog.domain.member.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateSignupRequest(
    @field:NotBlank
    @field:Size(max = 20)
    val licenseNumber: String,
)

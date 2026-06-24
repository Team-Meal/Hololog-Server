package team.nongchun.hororog.domain.member.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateSchoolNameRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val schoolName: String,
)

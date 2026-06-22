package team.nongchun.hororog.domain.member.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import team.nongchun.hororog.domain.member.entity.Role

data class SignupRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    @field:Size(max = 50)
    val name: String,
    @field:NotBlank
    @field:Size(max = 255)
    val schoolName: String,
    @field:NotBlank
    @field:Size(min = 8, max = 255)
    val password: String,
    @field:NotNull
    val role: Role,
)

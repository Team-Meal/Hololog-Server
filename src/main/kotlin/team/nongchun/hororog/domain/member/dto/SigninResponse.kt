package team.nongchun.hororog.domain.member.dto

import team.nongchun.hororog.domain.member.entity.Role
import java.time.LocalDateTime

data class SigninResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: LocalDateTime,
    val refreshTokenExpiresAt: LocalDateTime,
    val role: Role,
)

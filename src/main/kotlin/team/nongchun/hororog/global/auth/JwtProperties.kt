package team.nongchun.hororog.global.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessExpiration: Long,
    val refreshExpiration: Long,
)

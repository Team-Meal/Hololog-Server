package team.nongchun.hororog.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kamis")
data class KamisProperties(
    val url: String,
    val apiKey: String,
    val certId: String,
)

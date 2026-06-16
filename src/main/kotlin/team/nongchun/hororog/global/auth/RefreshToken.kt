package team.nongchun.hororog.global.auth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("refreshToken")
class RefreshToken(
    @Id
    val userId: Long,
    val token: String,
    @TimeToLive
    val ttl: Long,
)

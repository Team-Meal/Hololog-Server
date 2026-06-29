package team.nongchun.hororog.domain.ingredient.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("kamisPrice")
class KamisPriceCache(
    @Id
    val itemName: String,
    val pricePerKg: Int,
    val unit: String,
    val baseDate: String,
    val previousPricePerKg: Int?,
    @TimeToLive
    val ttl: Long = 3600,
)

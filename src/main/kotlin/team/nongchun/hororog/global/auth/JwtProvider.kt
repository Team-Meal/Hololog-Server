package team.nongchun.hororog.global.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import team.nongchun.hororog.domain.member.entity.Role
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    companion object {
        private const val ROLE_CLAIM = "role"
    }

    fun createAccessToken(
        userId: Long,
        role: Role,
    ): String = createToken(userId, role, jwtProperties.accessExpiration)

    fun createRefreshToken(userId: Long): String = createToken(userId, null, jwtProperties.refreshExpiration)

    fun getUserId(token: String): Long = parseClaims(token).subject.toLong()

    fun getRole(token: String): Role = Role.valueOf(parseClaims(token).get(ROLE_CLAIM, String::class.java))

    fun getExpiration(token: String): LocalDateTime =
        parseClaims(token)
            .expiration
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    fun validateToken(token: String): Boolean =
        try {
            parseClaims(token)
            true
        } catch (e: JwtException) {
            logger.info("유효하지 않은 JWT: {}", e.message)
            false
        } catch (e: IllegalArgumentException) {
            logger.info("비어있는 JWT: {}", e.message)
            false
        }

    private fun createToken(
        userId: Long,
        role: Role?,
        expiration: Long,
    ): String {
        val now = Date()
        val expiry = Date(now.time + expiration)
        return Jwts
            .builder()
            .subject(userId.toString())
            .apply { role?.let { claim(ROLE_CLAIM, it.name) } }
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    private fun parseClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}

package team.nongchun.hororog.global.auth

/**
 * JWT의 용도 구분. access 토큰으로만 API 인증이 가능하고, refresh 토큰은 재발급 전용이다.
 */
enum class TokenType {
    ACCESS,
    REFRESH,
}

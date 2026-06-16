package team.nongchun.hororog.global.auth

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository

/**
 * 사용자 ID 기반으로 [CustomUserDetails]를 로드하는 서비스.
 *
 * JWT 인증 흐름에서 토큰의 userId claim으로 데이터베이스의 회원 정보를 조회할 때 사용한다.
 * Spring Security의 `UserDetailsService` 인터페이스를 구현하지 않고 userId(Long)를 받는 독립 서비스다.
 */
@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) {
    fun loadUserByUsername(userId: Long): CustomUserDetails =
        memberRepository
            .findById(userId)
            .map(CustomUserDetails::from)
            .orElseThrow { MemberNotFoundException() }
}

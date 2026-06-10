package team.nongchun.hororog.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Member?
}

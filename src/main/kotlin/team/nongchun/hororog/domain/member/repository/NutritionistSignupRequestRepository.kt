package team.nongchun.hororog.domain.member.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.SignupStatus

interface NutritionistSignupRequestRepository : JpaRepository<NutritionistSignupRequest, Long> {
    fun existsByMemberIdAndStatus(
        memberId: Long,
        status: SignupStatus,
    ): Boolean

    @EntityGraph(attributePaths = ["member"])
    fun findByStatus(
        status: SignupStatus,
        pageable: Pageable,
    ): Page<NutritionistSignupRequest>
}

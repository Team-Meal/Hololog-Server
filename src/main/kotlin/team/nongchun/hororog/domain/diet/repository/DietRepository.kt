package team.nongchun.hororog.domain.diet.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.diet.entity.Diet

interface DietRepository : JpaRepository<Diet, Long> {
    fun findAllByMemberId(memberId: Long): List<Diet>
}

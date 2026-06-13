package team.nongchun.hororog.domain.leftover.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.leftover.entity.Leftover

interface LeftoverRepository : JpaRepository<Leftover, Long> {
    fun findByDietId(dietId: Long): Leftover?
}

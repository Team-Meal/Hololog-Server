package team.nongchun.hororog.domain.order.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.order.entity.OrderPlan

interface OrderPlanRepository : JpaRepository<OrderPlan, Long> {
    fun findAllByMemberSchoolNameOrderByIdDesc(schoolName: String): List<OrderPlan>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): OrderPlan?
}

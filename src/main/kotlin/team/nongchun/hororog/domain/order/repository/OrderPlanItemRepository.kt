package team.nongchun.hororog.domain.order.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.order.entity.OrderPlanItem

interface OrderPlanItemRepository : JpaRepository<OrderPlanItem, Long> {
    fun findAllByOrderPlanId(orderPlanId: Long): List<OrderPlanItem>

    fun findByIdAndOrderPlanId(
        id: Long,
        orderPlanId: Long,
    ): OrderPlanItem?

    fun findAllByIngredientId(ingredientId: Long): List<OrderPlanItem>
}

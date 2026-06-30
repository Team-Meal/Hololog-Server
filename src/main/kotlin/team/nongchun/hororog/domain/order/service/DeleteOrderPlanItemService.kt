package team.nongchun.hororog.domain.order.service

interface DeleteOrderPlanItemService {
    fun execute(
        orderPlanId: Long,
        itemId: Long,
    )
}

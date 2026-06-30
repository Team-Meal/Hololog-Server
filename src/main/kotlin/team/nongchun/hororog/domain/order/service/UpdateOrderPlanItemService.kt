package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse
import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanItemRequest

interface UpdateOrderPlanItemService {
    fun execute(
        orderPlanId: Long,
        itemId: Long,
        request: UpdateOrderPlanItemRequest,
    ): OrderPlanItemResponse
}

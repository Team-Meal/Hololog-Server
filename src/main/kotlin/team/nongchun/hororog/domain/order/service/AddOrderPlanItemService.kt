package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.AddOrderPlanItemRequest
import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse

interface AddOrderPlanItemService {
    fun execute(
        orderPlanId: Long,
        request: AddOrderPlanItemRequest,
    ): OrderPlanItemResponse
}

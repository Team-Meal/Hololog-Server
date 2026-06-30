package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanRequest

interface UpdateOrderPlanService {
    fun execute(
        orderPlanId: Long,
        request: UpdateOrderPlanRequest,
    )
}

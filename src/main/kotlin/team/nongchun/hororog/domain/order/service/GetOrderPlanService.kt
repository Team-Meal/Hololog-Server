package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.OrderPlanDetailResponse

interface GetOrderPlanService {
    fun execute(orderPlanId: Long): OrderPlanDetailResponse
}

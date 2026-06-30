package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.CreateOrderPlanRequest
import team.nongchun.hororog.domain.order.dto.OrderPlanDetailResponse

interface CreateOrderPlanService {
    fun execute(request: CreateOrderPlanRequest): OrderPlanDetailResponse
}

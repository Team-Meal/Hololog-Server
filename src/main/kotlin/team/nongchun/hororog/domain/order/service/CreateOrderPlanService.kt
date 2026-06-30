package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.CreateOrderPlanRequest

interface CreateOrderPlanService {
    fun execute(request: CreateOrderPlanRequest)
}

package team.nongchun.hororog.domain.order.service

import team.nongchun.hororog.domain.order.dto.OrderPlanListResponse

interface GetOrderPlanListService {
    fun execute(): List<OrderPlanListResponse>
}

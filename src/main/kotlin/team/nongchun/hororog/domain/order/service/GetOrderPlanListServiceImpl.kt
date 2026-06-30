package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.order.dto.OrderPlanListResponse
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetOrderPlanListServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetOrderPlanListService {
    override fun execute(): List<OrderPlanListResponse> {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        return orderPlanRepository
            .findAllByMemberSchoolNameOrderByIdDesc(schoolName)
            .map { OrderPlanListResponse.from(it) }
    }
}

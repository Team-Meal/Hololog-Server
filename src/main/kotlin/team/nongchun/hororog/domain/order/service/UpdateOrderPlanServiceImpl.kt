package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanRequest
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateOrderPlanServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateOrderPlanService {
    override fun execute(
        orderPlanId: Long,
        request: UpdateOrderPlanRequest,
    ) {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        val orderPlan =
            orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
                ?: throw OrderPlanNotFoundException()

        request.title?.let { orderPlan.title = it }
        request.planDate?.let { orderPlan.planDate = it }
        request.memo?.let { orderPlan.memo = it }

        request.studentCount?.let { newCount ->
            orderPlan.studentCount = newCount
            orderPlanItemRepository
                .findAllByOrderPlanId(orderPlanId)
                .forEach { OrderPlanCalculator.recalculate(it, newCount) }
        }
    }
}

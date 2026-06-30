package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class DeleteOrderPlanServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val authenticationHolder: AuthenticationHolder,
) : DeleteOrderPlanService {
    override fun execute(orderPlanId: Long) {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        val orderPlan =
            orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
                ?: throw OrderPlanNotFoundException()

        orderPlanItemRepository.deleteAllByOrderPlanId(orderPlanId)
        orderPlanRepository.delete(orderPlan)
    }
}

package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.order.exception.OrderPlanItemNotFoundException
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class DeleteOrderPlanItemServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val authenticationHolder: AuthenticationHolder,
) : DeleteOrderPlanItemService {
    override fun execute(
        orderPlanId: Long,
        itemId: Long,
    ) {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
            ?: throw OrderPlanNotFoundException()

        val item =
            orderPlanItemRepository.findByIdAndOrderPlanId(itemId, orderPlanId)
                ?: throw OrderPlanItemNotFoundException()

        orderPlanItemRepository.delete(item)
    }
}

package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.exception.InvalidQuantityUnitException
import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse
import team.nongchun.hororog.domain.order.dto.UpdateOrderPlanItemRequest
import team.nongchun.hororog.domain.order.exception.OrderPlanItemNotFoundException
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit

@Service
@Transactional
class UpdateOrderPlanItemServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateOrderPlanItemService {
    override fun execute(
        orderPlanId: Long,
        itemId: Long,
        request: UpdateOrderPlanItemRequest,
    ): OrderPlanItemResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        val orderPlan =
            orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
                ?: throw OrderPlanNotFoundException()

        val item =
            orderPlanItemRepository.findByIdAndOrderPlanId(itemId, orderPlanId)
                ?: throw OrderPlanItemNotFoundException()

        request.menuName?.let { item.menuName = it }
        request.unit?.let {
            item.unit = QuantityUnit.fromOrNull(it) ?: throw InvalidQuantityUnitException()
        }
        request.supplierName?.let { item.supplierName = it }
        request.unitPrice?.let { item.unitPrice = it }

        request.perPersonUsage?.let {
            item.perPersonUsage = it
            OrderPlanCalculator.recalculate(item, orderPlan.studentCount)
        } ?: run {
            if (request.unitPrice != null) {
                item.estimatedCost = item.unitPrice?.let { item.orderQuantity * it }
            }
        }

        return OrderPlanItemResponse.from(item)
    }
}

package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.cache.KamisPriceCacheRepository
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.InvalidQuantityUnitException
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.order.dto.AddOrderPlanItemRequest
import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse
import team.nongchun.hororog.domain.order.entity.OrderPlanItem
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit

@Service
@Transactional
class AddOrderPlanItemServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val ingredientRepository: IngredientRepository,
    private val kamisPriceCacheRepository: KamisPriceCacheRepository,
    private val authenticationHolder: AuthenticationHolder,
) : AddOrderPlanItemService {
    override fun execute(
        orderPlanId: Long,
        request: AddOrderPlanItemRequest,
    ): OrderPlanItemResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()

        val orderPlan =
            orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
                ?: throw OrderPlanNotFoundException()

        val ingredient =
            ingredientRepository.findByIdAndMemberSchoolName(request.ingredientId, schoolName)
                ?: throw IngredientNotFoundException()

        val unit = QuantityUnit.fromOrNull(request.unit) ?: throw InvalidQuantityUnitException()
        val unitPrice =
            request.unitPrice
                ?: kamisPriceCacheRepository
                    .findById(ingredient.name)
                    .orElse(null)
                    ?.pricePerKg
                    ?.toDouble()

        val requiredQuantity = request.perPersonUsage * orderPlan.studentCount
        val currentStock = ingredient.unit.convertTo(unit, ingredient.quantity.toDouble())
        val shortageQuantity = maxOf(requiredQuantity - currentStock, 0.0)
        val orderQuantity = shortageQuantity * 1.05
        val estimatedCost = unitPrice?.let { orderQuantity * it }

        val item =
            orderPlanItemRepository.save(
                OrderPlanItem(
                    orderPlan = orderPlan,
                    ingredient = ingredient,
                    menuName = request.menuName,
                    perPersonUsage = request.perPersonUsage,
                    unit = unit,
                    requiredQuantity = requiredQuantity,
                    currentStock = currentStock,
                    shortageQuantity = shortageQuantity,
                    orderQuantity = orderQuantity,
                    supplierName = request.supplierName,
                    unitPrice = unitPrice,
                    estimatedCost = estimatedCost,
                    basis = "${request.menuName} / 1인 ${request.perPersonUsage}${unit.name} × ${orderPlan.studentCount}명",
                ),
            )

        return OrderPlanItemResponse.from(item)
    }
}

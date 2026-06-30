package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientRequest
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.service.OrderPlanCalculator
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit
import team.nongchun.hororog.global.exception.InvalidQuantityUnitException

@Service
@Transactional
class UpdateIngredientServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateIngredientService {
    override fun execute(
        ingredientId: Long,
        request: UpdateIngredientRequest,
    ): IngredientUpdateResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val ingredient =
            ingredientRepository.findByIdAndMemberSchoolName(ingredientId, schoolName)
                ?: throw IngredientNotFoundException()

        request.name?.let { ingredient.name = it }
        request.unit?.let {
            ingredient.unit = QuantityUnit.fromOrNull(it) ?: throw InvalidQuantityUnitException()
        }
        request.expirationDate?.let { ingredient.expirationDate = it }
        request.category?.let { ingredient.category = it }

        request.quantity?.let { newQty ->
            ingredient.quantity = newQty
            orderPlanItemRepository.findAllByIngredientId(ingredient.id).forEach { item ->
                item.currentStock = ingredient.unit.convertTo(item.unit, newQty.toDouble())
                OrderPlanCalculator.recalculateStock(item)
            }
        }

        return IngredientUpdateResponse.from(ingredientRepository.saveAndFlush(ingredient))
    }
}

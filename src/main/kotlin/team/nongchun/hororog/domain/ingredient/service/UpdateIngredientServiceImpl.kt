package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientUpdateResponse
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientRequest
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.exception.InvalidQuantityUnitException
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit

@Service
@Transactional
class UpdateIngredientServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateIngredientService {
    override fun execute(
        ingredientId: Long,
        request: UpdateIngredientRequest,
    ): IngredientUpdateResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val ingredient =
            ingredientRepository
                .findById(ingredientId)
                .orElseThrow { IngredientNotFoundException() }
        if (ingredient.member.schoolName != schoolName) {
            throw IngredientNotFoundException()
        }

        request.name?.let { ingredient.name = it }
        request.quantity?.let { ingredient.quantity = it }
        request.unit?.let {
            ingredient.unit =
                try {
                    QuantityUnit.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw InvalidQuantityUnitException()
                }
        }
        request.expirationDate?.let { ingredient.expirationDate = it }
        request.category?.let { ingredient.category = it }

        return IngredientUpdateResponse.from(ingredientRepository.saveAndFlush(ingredient))
    }
}

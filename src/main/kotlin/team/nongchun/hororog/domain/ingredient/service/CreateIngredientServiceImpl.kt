package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientRequest
import team.nongchun.hororog.domain.ingredient.entity.Ingredient
import team.nongchun.hororog.domain.ingredient.exception.InvalidQuantityUnitException
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import team.nongchun.hororog.global.common.QuantityUnit

@Service
@Transactional
class CreateIngredientServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateIngredientService {
    override fun execute(request: CreateIngredientRequest) {
        val member =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
        val unit =
            try {
                QuantityUnit.valueOf(request.unit.uppercase())
            } catch (e: IllegalArgumentException) {
                throw InvalidQuantityUnitException()
            }
        ingredientRepository.save(
            Ingredient(
                member = member,
                name = request.name,
                quantity = request.quantity,
                unit = unit,
                expirationDate = request.expirationDate,
                category = request.category,
            ),
        )
    }
}

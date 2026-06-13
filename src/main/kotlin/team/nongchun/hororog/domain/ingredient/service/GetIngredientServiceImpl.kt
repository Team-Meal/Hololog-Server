package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientResponse
import team.nongchun.hororog.domain.ingredient.exception.IngredientNotFoundException
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetIngredientServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetIngredientService {
    override fun execute(ingredientId: Long): IngredientResponse {
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
        return IngredientResponse.from(ingredient)
    }
}

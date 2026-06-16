package team.nongchun.hororog.domain.ingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.ingredient.dto.IngredientListResponse
import team.nongchun.hororog.domain.ingredient.repository.IngredientRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetIngredientListServiceImpl(
    private val ingredientRepository: IngredientRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetIngredientListService {
    override fun execute(): List<IngredientListResponse> {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        return ingredientRepository
            .findAllByMemberSchoolName(schoolName)
            .map(IngredientListResponse::from)
    }
}

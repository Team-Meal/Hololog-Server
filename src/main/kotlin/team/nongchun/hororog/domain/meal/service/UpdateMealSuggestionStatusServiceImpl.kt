package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.UpdateMealSuggestionStatusRequest
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateMealSuggestionStatusServiceImpl(
    private val mealSuggestionRepository: MealSuggestionRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateMealSuggestionStatusService {
    override fun execute(
        suggestionId: Long,
        request: UpdateMealSuggestionStatusRequest,
    ) {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val suggestion =
            mealSuggestionRepository.findByIdAndMemberSchoolName(suggestionId, schoolName)
                ?: throw MealSuggestionNotFoundException()

        suggestion.status = request.mealSuggestionStatus
    }
}

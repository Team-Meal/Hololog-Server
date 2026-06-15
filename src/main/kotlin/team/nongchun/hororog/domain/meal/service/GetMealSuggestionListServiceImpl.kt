package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.MealSuggestionResponse
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetMealSuggestionListServiceImpl(
    private val mealSuggestionRepository: MealSuggestionRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetMealSuggestionListService {
    override fun execute(): List<MealSuggestionResponse> {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        return mealSuggestionRepository
            .findAllByMemberSchoolNameOrderByIdDesc(schoolName)
            .map(MealSuggestionResponse::from)
    }
}

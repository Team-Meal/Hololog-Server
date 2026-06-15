package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.CreateMealSuggestionRequest
import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateMealSuggestionServiceImpl(
    private val mealSuggestionRepository: MealSuggestionRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateMealSuggestionService {
    override fun execute(request: CreateMealSuggestionRequest) {
        val member =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }

        mealSuggestionRepository.save(
            MealSuggestion(
                member = member,
                title = request.title,
                content = request.content,
                status = request.mealSuggestionStatus,
            ),
        )
    }
}

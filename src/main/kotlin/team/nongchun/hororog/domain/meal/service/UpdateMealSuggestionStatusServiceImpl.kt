package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.UpdateMealSuggestionStatusRequest
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import team.nongchun.hororog.domain.meal.exception.InvalidMealSuggestionStatusException
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateMealSuggestionStatusServiceImpl(
    private val mealSuggestionRepository: MealSuggestionRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateMealSuggestionStatusService {
    override fun execute(
        suggestionId: Long,
        request: UpdateMealSuggestionStatusRequest,
    ) {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val suggestion =
            mealSuggestionRepository.findByIdAndMemberSchoolName(suggestionId, schoolName)
                ?: throw MealSuggestionNotFoundException()

        if (suggestion.status != SuggestionStatus.PENDING ||
            request.mealSuggestionStatus == SuggestionStatus.PENDING
        ) {
            throw InvalidMealSuggestionStatusException()
        }

        suggestion.status = request.mealSuggestionStatus
    }
}

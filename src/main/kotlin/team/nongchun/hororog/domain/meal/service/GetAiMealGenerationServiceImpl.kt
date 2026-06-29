package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.MealAiGenerationResponse
import team.nongchun.hororog.domain.meal.exception.MealAiGenerationNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealAiGenerationRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetAiMealGenerationServiceImpl(
    private val mealAiGenerationRepository: MealAiGenerationRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetAiMealGenerationService {
    override fun execute(id: Long): MealAiGenerationResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val generation =
            mealAiGenerationRepository.findById(id).orElseThrow { MealAiGenerationNotFoundException() }
        if (generation.schoolName != schoolName) throw MealAiGenerationNotFoundException()
        return MealAiGenerationResponse.from(generation)
    }
}

package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.CreateAiMealRequest
import team.nongchun.hororog.domain.meal.exception.MealNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import tools.jackson.databind.ObjectMapper
import kotlin.math.roundToInt

@Service
@Transactional
class CreateAiMealServiceImpl(
    private val mealRepository: MealRepository,
    private val authenticationHolder: AuthenticationHolder,
    private val objectMapper: ObjectMapper,
) : CreateAiMealService {
    override fun execute(request: CreateAiMealRequest): Long {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val meal =
            mealRepository.findByIdAndMemberSchoolName(request.dietId, schoolName)
                ?: throw MealNotFoundException()

        meal.name = request.menuName
        meal.totalCalories = request.kcal.roundToInt()
        meal.nutritionInfo =
            objectMapper.writeValueAsString(
                mapOf(
                    "protein" to request.protein,
                    "fat" to request.fat,
                    "sodium" to request.sodium,
                ),
            )
        return meal.id
    }
}

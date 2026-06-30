package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanRequest
import team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest
import team.nongchun.hororog.domain.meal.dto.MealAiGenerationResponse
import team.nongchun.hororog.domain.meal.entity.MealAiGeneration
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.PENDING
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.RUNNING
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.SUCCEEDED
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadyInProgressException
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadySucceededException
import team.nongchun.hororog.domain.meal.repository.MealAiGenerationRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class GenerateAiMealPlanServiceImpl(
    private val mealAiGenerationRepository: MealAiGenerationRepository,
    private val authenticationHolder: AuthenticationHolder,
    private val asyncService: AiMealGenerationAsyncService,
) : GenerateAiMealPlanService {
    override fun execute(
        authorization: String,
        request: GenerateAiMealPlanRequest,
    ): MealAiGenerationResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val userId = authenticationHolder.getCurrentUserId()

        mealAiGenerationRepository
            .findBySchoolNameAndMonthAndStatusIn(
                schoolName = schoolName,
                month = request.month,
                statuses = listOf(PENDING, RUNNING),
            )?.let { throw AiMealGenerationAlreadyInProgressException() }

        mealAiGenerationRepository
            .findBySchoolNameAndMonthAndStatus(
                schoolName = schoolName,
                month = request.month,
                status = SUCCEEDED,
            )?.let { throw AiMealGenerationAlreadySucceededException() }

        val generation =
            mealAiGenerationRepository.save(
                MealAiGeneration(
                    schoolName = schoolName,
                    month = request.month,
                ),
            )

        asyncService.run(
            generationId = generation.id,
            authorization = authorization,
            aiRequest =
                AiGeneratePlanRequest(
                    month = request.month,
                    schoolId = userId,
                    holidays = request.holidays.map { it.toString() },
                ),
        )

        return MealAiGenerationResponse.from(generation)
    }
}

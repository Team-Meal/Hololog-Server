package team.nongchun.hororog.domain.meal.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.client.AiServerClient
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanRequest
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.FAILED
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.RUNNING
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.SUCCEEDED
import team.nongchun.hororog.domain.meal.exception.MealAiGenerationNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealAiGenerationRepository

@Service
class AiMealGenerationAsyncService(
    private val mealAiGenerationRepository: MealAiGenerationRepository,
    private val aiServerClient: AiServerClient,
) {
    @Async("aiGenerationExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun run(
        generationId: Long,
        authorization: String,
        aiRequest: AiGeneratePlanRequest,
    ) {
        val generation = mealAiGenerationRepository.findById(generationId).orElseThrow { MealAiGenerationNotFoundException() }
        generation.status = RUNNING
        mealAiGenerationRepository.save(generation)
        try {
            aiServerClient.generatePlan(authorization, aiRequest)
            generation.status = SUCCEEDED
        } catch (e: Exception) {
            generation.status = FAILED
            generation.errorMessage = e.message
        }
        mealAiGenerationRepository.save(generation)
    }
}

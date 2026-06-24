package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import team.nongchun.hororog.domain.meal.client.AiServerClient
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanRequest
import team.nongchun.hororog.domain.meal.dto.AiMealPlanResponse
import team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
class GenerateAiMealPlanServiceImpl(
    private val aiServerClient: AiServerClient,
    private val authenticationHolder: AuthenticationHolder,
) : GenerateAiMealPlanService {
    override fun execute(
        authorization: String,
        request: GenerateAiMealPlanRequest,
    ): AiMealPlanResponse {
        val aiRequest =
            AiGeneratePlanRequest(
                month = request.month,
                schoolId = authenticationHolder.getCurrentUserId(),
                holidays = request.holidays.map { it.toString() },
            )
        val response = aiServerClient.generatePlan(authorization, aiRequest)
        return AiMealPlanResponse.from(response)
    }
}

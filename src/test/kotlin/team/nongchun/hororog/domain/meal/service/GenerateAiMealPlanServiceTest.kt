package team.nongchun.hororog.domain.meal.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.meal.client.AiServerClient
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanRequest
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanResponse
import team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate

class GenerateAiMealPlanServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val aiServerClient = mockk<AiServerClient>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = GenerateAiMealPlanServiceImpl(aiServerClient, authenticationHolder)

        Given("AI 월간 식단 생성을 요청할 때") {
            val capturedRequest = slot<AiGeneratePlanRequest>()
            every { authenticationHolder.getCurrentUserId() } returns 1L
            every {
                aiServerClient.generatePlan("Bearer token", capture(capturedRequest))
            } returns AiGeneratePlanResponse(month = "2026-06", totalMeals = 20)

            When("서비스를 실행하면") {
                val result =
                    service.execute(
                        authorization = "Bearer token",
                        request =
                            GenerateAiMealPlanRequest(
                                month = "2026-06",
                                holidays = listOf(LocalDate.of(2026, 6, 6)),
                            ),
                    )

                Then("AI 서버 계약에 맞춰 school_id로 현재 사용자 ID를 전달한다") {
                    result.month shouldBe "2026-06"
                    capturedRequest.captured.schoolId shouldBe 1L
                    capturedRequest.captured.holidays shouldBe listOf("2026-06-06")
                    verify(exactly = 1) { authenticationHolder.getCurrentUserId() }
                }
            }
        }
    })

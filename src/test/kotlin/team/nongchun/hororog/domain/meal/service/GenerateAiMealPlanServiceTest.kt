package team.nongchun.hororog.domain.meal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.meal.entity.MealAiGeneration
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.PENDING
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.RUNNING
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus.SUCCEEDED
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadyInProgressException
import team.nongchun.hororog.domain.meal.exception.AiMealGenerationAlreadySucceededException
import team.nongchun.hororog.domain.meal.repository.MealAiGenerationRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class GenerateAiMealPlanServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealAiGenerationRepository = mockk<MealAiGenerationRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val asyncService = mockk<AiMealGenerationAsyncService>(relaxed = true)
        val service =
            GenerateAiMealPlanServiceImpl(
                mealAiGenerationRepository,
                memberRepository,
                authenticationHolder,
                asyncService,
            )

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        val request =
            team.nongchun.hororog.domain.meal.dto.GenerateAiMealPlanRequest(
                month = "2026-06",
                holidays = listOf(LocalDate.of(2026, 6, 6)),
            )

        Given("진행 중인 작업이 없고 성공한 작업도 없을 때") {
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every {
                mealAiGenerationRepository.findBySchoolNameAndMonthAndStatusIn(
                    "농촌초등학교",
                    "2026-06",
                    listOf(PENDING, RUNNING),
                )
            } returns null
            every {
                mealAiGenerationRepository.findBySchoolNameAndMonthAndStatus(
                    "농촌초등학교",
                    "2026-06",
                    SUCCEEDED,
                )
            } returns null
            val generation = MealAiGeneration(id = 10L, schoolName = "농촌초등학교", month = "2026-06")
            every { mealAiGenerationRepository.save(any()) } returns generation

            When("AI 식단 생성을 요청하면") {
                val result = service.execute("Bearer token", request)

                Then("PENDING 레코드가 저장되고 비동기 작업이 시작된다") {
                    result.id shouldBe 10L
                    result.status shouldBe PENDING
                    verify(exactly = 1) { mealAiGenerationRepository.save(any()) }
                    verify(exactly = 1) { asyncService.run(10L, "Bearer token", any()) }
                }
            }
        }

        Given("같은 학교, 같은 월에 PENDING 작업이 있을 때") {
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every {
                mealAiGenerationRepository.findBySchoolNameAndMonthAndStatusIn(
                    "농촌초등학교",
                    "2026-06",
                    listOf(PENDING, RUNNING),
                )
            } returns MealAiGeneration(schoolName = "농촌초등학교", month = "2026-06", status = PENDING)

            When("AI 식단 생성을 요청하면") {
                Then("AiMealGenerationAlreadyInProgressException이 발생한다") {
                    shouldThrow<AiMealGenerationAlreadyInProgressException> {
                        service.execute("Bearer token", request)
                    }
                    verify(exactly = 0) { mealAiGenerationRepository.save(any()) }
                }
            }
        }

        Given("같은 학교, 같은 월에 SUCCEEDED 작업이 있을 때") {
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every {
                mealAiGenerationRepository.findBySchoolNameAndMonthAndStatusIn(
                    "농촌초등학교",
                    "2026-06",
                    listOf(PENDING, RUNNING),
                )
            } returns null
            every {
                mealAiGenerationRepository.findBySchoolNameAndMonthAndStatus(
                    "농촌초등학교",
                    "2026-06",
                    SUCCEEDED,
                )
            } returns MealAiGeneration(schoolName = "농촌초등학교", month = "2026-06", status = SUCCEEDED)

            When("AI 식단 생성을 요청하면") {
                Then("AiMealGenerationAlreadySucceededException이 발생한다") {
                    shouldThrow<AiMealGenerationAlreadySucceededException> {
                        service.execute("Bearer token", request)
                    }
                    verify(exactly = 0) { mealAiGenerationRepository.save(any()) }
                }
            }
        }
    })

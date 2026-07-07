package team.nongchun.hororog.domain.meal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.meal.dto.CreateAiMealRequest
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.exception.MealNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.global.auth.AuthenticationHolder
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime

class CreateAiMealServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealRepository = mockk<MealRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val objectMapper = mockk<ObjectMapper>()
        val service = CreateAiMealServiceImpl(mealRepository, authenticationHolder, objectMapper)

        val request =
            CreateAiMealRequest(
                dietId = 10L,
                menuName = "현미밥",
                kcal = 123.6,
                protein = 10.0,
                fat = 5.0,
                sodium = 300.0,
            )

        Given("같은 학교의 급식 슬롯이 있을 때") {
            val member =
                Member(
                    id = 1L,
                    email = "nutritionist@hororog.team",
                    password = "password",
                    name = "김영양",
                    schoolName = "농촌초등학교",
                    role = Role.NUTRITIONIST,
                )
            val meal =
                Meal(
                    id = 10L,
                    member = member,
                    name = "",
                    mealType = MealType.LUNCH,
                    mealDate = LocalDateTime.of(2026, 6, 29, 0, 0),
                )
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns meal
            every { objectMapper.writeValueAsString(any()) } returns """{"protein":10.0,"fat":5.0,"sodium":300.0}"""

            When("AI 메뉴 정보를 채우면") {
                val result = service.execute(request)

                Then("학교 조건으로 찾은 슬롯만 갱신한다") {
                    result shouldBe 10L
                    meal.name shouldBe "현미밥"
                    meal.totalCalories shouldBe 124
                    meal.nutritionInfo shouldBe """{"protein":10.0,"fat":5.0,"sodium":300.0}"""
                    verify(exactly = 1) { mealRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") }
                }
            }
        }

        Given("같은 학교의 급식 슬롯이 없을 때") {
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns null

            When("AI 메뉴 정보를 채우면") {
                Then("급식 없음 예외가 발생한다") {
                    shouldThrow<MealNotFoundException> {
                        service.execute(request)
                    }
                    verify(exactly = 1) { mealRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") }
                    verify(exactly = 0) { objectMapper.writeValueAsString(any()) }
                }
            }
        }
    })

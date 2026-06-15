package team.nongchun.hororog.domain.meal.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.time.ZoneId

class GetTodayMealServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealRepository = mockk<MealRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = GetTodayMealServiceImpl(mealRepository, authenticationHolder)

        val student =
            Member(
                id = 1L,
                email = "student@hororog.team",
                password = "encoded",
                name = "김학생",
                schoolName = "농촌초등학교",
                role = Role.STUDENT,
            )

        Given("오늘 점심 급식이 존재할 때") {
            val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
            val meals =
                listOf(
                    Meal(
                        id = 1L,
                        member = student,
                        name = "현미밥",
                        mealType = MealType.LUNCH,
                        mealDate = today.atTime(12, 0),
                        totalCalories = 300,
                        nutritionInfo = "탄수화물",
                        originInfo = "쌀: 국내산",
                    ),
                    Meal(
                        id = 2L,
                        member = student,
                        name = "된장국",
                        mealType = MealType.LUNCH,
                        mealDate = today.atTime(12, 0),
                        totalCalories = 120,
                        nutritionInfo = "단백질",
                        originInfo = "대두: 국내산",
                    ),
                )
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every {
                mealRepository.findAllByMemberSchoolNameAndMealTypeAndMealDateGreaterThanEqualAndMealDateLessThanOrderByIdAsc(
                    "농촌초등학교",
                    MealType.LUNCH,
                    any(),
                    any(),
                )
            } returns meals

            When("오늘 급식을 조회하면") {
                val result = service.execute(MealType.LUNCH)

                Then("오늘 날짜와 메뉴 정보를 반환한다") {
                    result.mealDate shouldBe today
                    result.mealType shouldBe MealType.LUNCH
                    result.menuNames shouldContainExactly listOf("현미밥", "된장국")
                    result.calorie shouldBe "420"
                    result.nutritionInfo shouldBe "탄수화물\n단백질"
                    result.originInfo shouldBe "쌀: 국내산\n대두: 국내산"
                    verify(exactly = 1) {
                        mealRepository.findAllByMemberSchoolNameAndMealTypeAndMealDateGreaterThanEqualAndMealDateLessThanOrderByIdAsc(
                            "농촌초등학교",
                            MealType.LUNCH,
                            today.atStartOfDay(),
                            today.plusDays(1).atStartOfDay(),
                        )
                    }
                }
            }
        }
    })

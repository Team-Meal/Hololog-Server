package team.nongchun.hororog.domain.meal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import java.time.LocalDate
import java.util.Optional

class CreateAiMealSlotServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealRepository = mockk<MealRepository>()
        val memberRepository = mockk<MemberRepository>()
        val service = CreateAiMealSlotServiceImpl(mealRepository, memberRepository)

        Given("AI 콜백의 school_id 회원이 존재할 때") {
            val member =
                Member(
                    id = 1L,
                    email = "nutritionist@hororog.team",
                    password = "password",
                    name = "김영양",
                    schoolName = "농촌초등학교",
                    role = Role.NUTRITIONIST,
                )
            val mealSlot = slot<Meal>()
            every { memberRepository.findById(1L) } returns Optional.of(member)
            every { mealRepository.save(capture(mealSlot)) } answers { mealSlot.captured }

            When("급식 슬롯을 생성하면") {
                Then("해당 회원 학교의 빈 급식 슬롯을 저장한다") {
                    val id =
                        service.execute(
                            CreateAiMealSlotRequest(
                                date = LocalDate.of(2026, 6, 29),
                                mealType = MealType.LUNCH,
                                schoolId = 1L,
                            ),
                        )

                    id shouldBe 0L
                    mealSlot.captured.member shouldBe member
                    mealSlot.captured.name shouldBe ""
                    mealSlot.captured.mealType shouldBe MealType.LUNCH
                    verify(exactly = 1) { memberRepository.findById(1L) }
                    verify(exactly = 1) { mealRepository.save(any()) }
                }
            }
        }

        Given("AI 콜백의 school_id 회원이 없을 때") {
            every { memberRepository.findById(99L) } returns Optional.empty()

            When("급식 슬롯을 생성하면") {
                Then("슬롯을 저장하지 않고 예외가 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(
                            CreateAiMealSlotRequest(
                                date = LocalDate.of(2026, 6, 29),
                                mealType = MealType.LUNCH,
                                schoolId = 99L,
                            ),
                        )
                    }
                    verify(exactly = 1) { memberRepository.findById(99L) }
                    verify(exactly = 0) { mealRepository.save(any()) }
                }
            }
        }
    })

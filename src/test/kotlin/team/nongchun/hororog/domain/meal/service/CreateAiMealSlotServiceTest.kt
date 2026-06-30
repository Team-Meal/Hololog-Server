package team.nongchun.hororog.domain.meal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate

class CreateAiMealSlotServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealRepository = mockk<MealRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = CreateAiMealSlotServiceImpl(mealRepository, memberRepository, authenticationHolder)

        Given("AI 콜백의 school_id가 현재 사용자 ID와 다를 때") {
            every { authenticationHolder.getCurrentUserId() } returns 1L

            When("급식 슬롯을 생성하면") {
                Then("슬롯을 저장하지 않고 예외가 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(
                            CreateAiMealSlotRequest(
                                date = LocalDate.of(2026, 6, 29),
                                mealType = MealType.LUNCH,
                                schoolId = 2L,
                            ),
                        )
                    }
                    verify(exactly = 0) { memberRepository.findById(any()) }
                    verify(exactly = 0) { mealRepository.save(any()) }
                }
            }
        }
    })

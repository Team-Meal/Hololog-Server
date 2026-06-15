package team.nongchun.hororog.domain.meal.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.meal.dto.CreateMealSuggestionRequest
import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.util.Optional

class CreateMealSuggestionServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealSuggestionRepository = mockk<MealSuggestionRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = CreateMealSuggestionServiceImpl(mealSuggestionRepository, memberRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "student@hororog.team",
                password = "encoded",
                name = "김학생",
                schoolName = "농촌초등학교",
                role = Role.STUDENT,
            )

        Given("먹고 싶은 급식 추천 요청이 주어졌을 때") {
            val request =
                CreateMealSuggestionRequest(
                    title = "제육볶음",
                    content = "매운맛으로 먹고 싶어요.",
                )
            val suggestionSlot = slot<MealSuggestion>()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { mealSuggestionRepository.save(capture(suggestionSlot)) } answers { suggestionSlot.captured }

            When("추천을 생성하면") {
                service.execute(request)

                Then("현재 회원에게 연결된 급식 추천을 저장한다") {
                    suggestionSlot.captured.member shouldBe member
                    suggestionSlot.captured.title shouldBe "제육볶음"
                    suggestionSlot.captured.content shouldBe "매운맛으로 먹고 싶어요."
                    suggestionSlot.captured.status shouldBe SuggestionStatus.PENDING
                    verify(exactly = 1) { mealSuggestionRepository.save(any()) }
                }
            }
        }
    })

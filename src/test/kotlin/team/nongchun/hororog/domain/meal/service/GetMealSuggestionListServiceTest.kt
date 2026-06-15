package team.nongchun.hororog.domain.meal.service

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDateTime

class GetMealSuggestionListServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealSuggestionRepository = mockk<MealSuggestionRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = GetMealSuggestionListServiceImpl(mealSuggestionRepository, authenticationHolder)

        val student =
            Member(
                id = 2L,
                email = "student@hororog.team",
                password = "encoded",
                name = "김학생",
                schoolName = "농촌초등학교",
                role = Role.STUDENT,
            )

        Given("같은 학교 급식 추천이 존재할 때") {
            val createdAt = LocalDateTime.of(2026, 6, 15, 12, 0)
            val suggestion =
                MealSuggestion(
                    id = 3L,
                    member = student,
                    title = "카레",
                    content = null,
                    status = SuggestionStatus.PENDING,
                ).apply {
                    this.createdAt = createdAt
                    this.updatedAt = createdAt
                }
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealSuggestionRepository.findAllByMemberSchoolNameOrderByIdDesc("농촌초등학교") } returns listOf(suggestion)

            When("급식 추천 목록을 조회하면") {
                val result = service.execute()

                Then("현재 영양사의 학교 기준으로 조회하고 응답 DTO로 변환한다") {
                    result.map { it.id } shouldContainExactly listOf(3L)
                    result[0].title shouldBe "카레"
                    result[0].content shouldBe null
                    result[0].mealSuggestionStatus shouldBe SuggestionStatus.PENDING
                    verify(exactly = 1) {
                        mealSuggestionRepository.findAllByMemberSchoolNameOrderByIdDesc("농촌초등학교")
                    }
                }
            }
        }
    })

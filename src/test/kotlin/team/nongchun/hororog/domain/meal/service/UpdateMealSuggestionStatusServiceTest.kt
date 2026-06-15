package team.nongchun.hororog.domain.meal.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import team.nongchun.hororog.domain.meal.dto.UpdateMealSuggestionStatusRequest
import team.nongchun.hororog.domain.meal.entity.MealSuggestion
import team.nongchun.hororog.domain.meal.entity.SuggestionStatus
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.util.Optional

class UpdateMealSuggestionStatusServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealSuggestionRepository = mockk<MealSuggestionRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = UpdateMealSuggestionStatusServiceImpl(mealSuggestionRepository, memberRepository, authenticationHolder)

        val nutritionist =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )
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
            val suggestion =
                MealSuggestion(
                    id = 3L,
                    member = student,
                    title = "카레",
                    content = null,
                    status = SuggestionStatus.PENDING,
                )
            every { authenticationHolder.getCurrentUserId() } returns nutritionist.id
            every { memberRepository.findById(nutritionist.id) } returns Optional.of(nutritionist)
            every { mealSuggestionRepository.findByIdAndMemberSchoolName(3L, "농촌초등학교") } returns suggestion

            When("처리 상태를 변경하면") {
                service.execute(3L, UpdateMealSuggestionStatusRequest(SuggestionStatus.APPROVED))

                Then("급식 추천 상태가 변경된다") {
                    suggestion.status shouldBe SuggestionStatus.APPROVED
                }
            }
        }

        Given("같은 학교 급식 추천이 존재하지 않을 때") {
            every { authenticationHolder.getCurrentUserId() } returns nutritionist.id
            every { memberRepository.findById(nutritionist.id) } returns Optional.of(nutritionist)
            every { mealSuggestionRepository.findByIdAndMemberSchoolName(3L, "농촌초등학교") } returns null

            When("처리 상태를 변경하면") {
                Then("MealSuggestionNotFoundException이 발생한다") {
                    shouldThrow<MealSuggestionNotFoundException> {
                        service.execute(3L, UpdateMealSuggestionStatusRequest(SuggestionStatus.REJECTED))
                    }
                }
            }
        }
    })

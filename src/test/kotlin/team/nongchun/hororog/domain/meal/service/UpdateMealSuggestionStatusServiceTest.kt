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
import team.nongchun.hororog.domain.meal.exception.InvalidMealSuggestionStatusException
import team.nongchun.hororog.domain.meal.exception.MealSuggestionNotFoundException
import team.nongchun.hororog.domain.meal.repository.MealSuggestionRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.global.auth.AuthenticationHolder

class UpdateMealSuggestionStatusServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val mealSuggestionRepository = mockk<MealSuggestionRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = UpdateMealSuggestionStatusServiceImpl(mealSuggestionRepository, authenticationHolder)

        val student =
            Member(
                id = 2L,
                email = "student@hororog.team",
                password = "encoded",
                name = "김학생",
                schoolName = "농촌초등학교",
                role = Role.STUDENT,
            )

        Given("PENDING 상태인 급식 추천이 존재할 때") {
            val suggestion =
                MealSuggestion(
                    id = 3L,
                    member = student,
                    title = "카레",
                    content = null,
                    status = SuggestionStatus.PENDING,
                )
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealSuggestionRepository.findByIdAndMemberSchoolName(3L, "농촌초등학교") } returns suggestion

            When("APPROVED로 상태를 변경하면") {
                service.execute(3L, UpdateMealSuggestionStatusRequest(SuggestionStatus.APPROVED))

                Then("급식 추천 상태가 변경된다") {
                    suggestion.status shouldBe SuggestionStatus.APPROVED
                }
            }
        }

        Given("이미 APPROVED 상태인 급식 추천이 존재할 때") {
            val suggestion =
                MealSuggestion(
                    id = 3L,
                    member = student,
                    title = "카레",
                    content = null,
                    status = SuggestionStatus.APPROVED,
                )
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealSuggestionRepository.findByIdAndMemberSchoolName(3L, "농촌초등학교") } returns suggestion

            When("상태를 변경하면") {
                Then("InvalidMealSuggestionStatusException이 발생한다") {
                    shouldThrow<InvalidMealSuggestionStatusException> {
                        service.execute(3L, UpdateMealSuggestionStatusRequest(SuggestionStatus.PENDING))
                    }
                }
            }
        }

        Given("PENDING 상태인 급식 추천이 존재할 때") {
            val suggestion =
                MealSuggestion(
                    id = 3L,
                    member = student,
                    title = "카레",
                    content = null,
                    status = SuggestionStatus.PENDING,
                )
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
            every { mealSuggestionRepository.findByIdAndMemberSchoolName(3L, "농촌초등학교") } returns suggestion

            When("PENDING으로 되돌리려 하면") {
                Then("InvalidMealSuggestionStatusException이 발생한다") {
                    shouldThrow<InvalidMealSuggestionStatusException> {
                        service.execute(3L, UpdateMealSuggestionStatusRequest(SuggestionStatus.PENDING))
                    }
                }
            }
        }

        Given("같은 학교 급식 추천이 존재하지 않을 때") {
            every { authenticationHolder.getCurrentUserSchoolName() } returns "농촌초등학교"
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

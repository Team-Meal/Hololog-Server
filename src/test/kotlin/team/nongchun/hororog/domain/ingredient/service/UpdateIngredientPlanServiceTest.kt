package team.nongchun.hororog.domain.ingredient.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.ingredient.dto.UpdateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan
import team.nongchun.hororog.domain.ingredient.exception.InvalidIngredientPlanException
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class UpdateIngredientPlanServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val ingredientPlanRepository = mockk<IngredientPlanRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = UpdateIngredientPlanServiceImpl(ingredientPlanRepository, memberRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        fun plan() =
            IngredientPlan(
                id = 10L,
                member = member,
                title = "6월 식자재 계획표",
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
                memo = "여름철 식자재 위주",
            )

        Given("부분 수정 요청이 주어졌을 때") {
            val plan = plan()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { ingredientPlanRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns plan
            every { ingredientPlanRepository.saveAndFlush(plan) } returns plan

            When("제목만 수정하면") {
                val response = service.execute(10L, UpdateIngredientPlanRequest(title = "수정된 계획표"))

                Then("날짜는 기존 값을 유지하고 id를 반환한다") {
                    response.ingredientPlanId shouldBe 10L
                    plan.title shouldBe "수정된 계획표"
                    plan.startDate shouldBe LocalDate.of(2026, 6, 1)
                    plan.endDate shouldBe LocalDate.of(2026, 6, 30)
                }
            }
        }

        Given("startDate만 변경해 기존 endDate와 역전될 때") {
            val plan = plan()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { ingredientPlanRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns plan

            When("식자재 계획표를 수정하면") {
                Then("InvalidIngredientPlanException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidIngredientPlanException> {
                        service.execute(10L, UpdateIngredientPlanRequest(startDate = LocalDate.of(2026, 7, 1)))
                    }
                    verify(exactly = 0) { ingredientPlanRepository.saveAndFlush(any()) }
                }
            }
        }

        Given("startDate, endDate 모두 변경하고 역전될 때") {
            val plan = plan()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { ingredientPlanRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns plan

            When("식자재 계획표를 수정하면") {
                Then("InvalidIngredientPlanException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidIngredientPlanException> {
                        service.execute(
                            10L,
                            UpdateIngredientPlanRequest(
                                startDate = LocalDate.of(2026, 8, 1),
                                endDate = LocalDate.of(2026, 7, 1),
                            ),
                        )
                    }
                    verify(exactly = 0) { ingredientPlanRepository.saveAndFlush(any()) }
                }
            }
        }
    })

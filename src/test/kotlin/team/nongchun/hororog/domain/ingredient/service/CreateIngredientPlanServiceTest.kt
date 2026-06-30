package team.nongchun.hororog.domain.ingredient.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.ingredient.dto.CreateIngredientPlanRequest
import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan
import team.nongchun.hororog.domain.ingredient.exception.InvalidIngredientPlanException
import team.nongchun.hororog.domain.ingredient.repository.IngredientPlanRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class CreateIngredientPlanServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val ingredientPlanRepository = mockk<IngredientPlanRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = CreateIngredientPlanServiceImpl(ingredientPlanRepository, memberRepository, authenticationHolder)

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
            CreateIngredientPlanRequest(
                title = "6월 식자재 계획표",
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
                memo = "여름철 식자재 위주",
            )

        Given("로그인한 회원이 존재할 때") {
            every { authenticationHolder.getCurrentUserId() } returns 1L
            every { memberRepository.findById(1L) } returns Optional.of(member)
            val saved = slot<IngredientPlan>()
            every { ingredientPlanRepository.save(capture(saved)) } answers { saved.captured }

            When("식자재 계획표를 작성하면") {
                service.execute(request)

                Then("요청 값으로 계획표가 저장된다") {
                    verify { ingredientPlanRepository.save(any()) }
                    saved.captured.member shouldBe member
                    saved.captured.title shouldBe "6월 식자재 계획표"
                    saved.captured.startDate shouldBe LocalDate.of(2026, 6, 1)
                    saved.captured.endDate shouldBe LocalDate.of(2026, 6, 30)
                    saved.captured.memo shouldBe "여름철 식자재 위주"
                }
            }
        }

        Given("로그인한 회원이 존재하지 않을 때") {
            every { authenticationHolder.getCurrentUserId() } returns 99L
            every { memberRepository.findById(99L) } returns Optional.empty()

            When("식자재 계획표를 작성하면") {
                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(request)
                    }
                }
            }
        }

        Given("시작일이 종료일보다 늦은 요청이 주어졌을 때") {
            val invalidRequest =
                request.copy(
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2026, 6, 30),
                )

            When("식자재 계획표를 생성하면") {
                Then("InvalidIngredientPlanException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidIngredientPlanException> {
                        service.execute(invalidRequest)
                    }
                    verify(exactly = 0) { ingredientPlanRepository.save(any()) }
                }
            }
        }
    })

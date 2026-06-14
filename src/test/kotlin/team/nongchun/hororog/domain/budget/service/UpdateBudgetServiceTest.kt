package team.nongchun.hororog.domain.budget.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.budget.dto.UpdateBudgetRequest
import team.nongchun.hororog.domain.budget.entity.Budget
import team.nongchun.hororog.domain.budget.exception.InvalidBudgetException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class UpdateBudgetServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val budgetRepository = mockk<BudgetRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = UpdateBudgetServiceImpl(budgetRepository, memberRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )

        fun budget() =
            Budget(
                id = 10L,
                member = member,
                name = "6월 급식 예산",
                totalBudget = 1_000_000,
                usedBudget = 100_000,
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
            )

        Given("부분 수정 요청이 주어졌을 때") {
            val budget = budget()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { budgetRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns budget

            When("전달된 필드만 수정하면") {
                val response =
                    service.execute(
                        10L,
                        UpdateBudgetRequest(
                            title = "수정된 예산",
                            usedAmount = 200_000,
                        ),
                    )

                Then("null 필드는 유지하고 id를 반환한다") {
                    response.id shouldBe 10L
                    budget.name shouldBe "수정된 예산"
                    budget.totalBudget shouldBe 1_000_000
                    budget.usedBudget shouldBe 200_000
                    budget.startDate shouldBe LocalDate.of(2026, 6, 1)
                    budget.endDate shouldBe LocalDate.of(2026, 6, 30)
                    verify(exactly = 0) { budgetRepository.saveAndFlush(any()) }
                }
            }
        }

        Given("부분 수정 결과 사용 금액이 전체 예산을 초과할 때") {
            val budget = budget()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { budgetRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns budget

            When("예산을 수정하면") {
                Then("InvalidBudgetException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidBudgetException> {
                        service.execute(10L, UpdateBudgetRequest(usedAmount = 1_000_001))
                    }
                    verify(exactly = 0) { budgetRepository.saveAndFlush(any()) }
                }
            }
        }
    })

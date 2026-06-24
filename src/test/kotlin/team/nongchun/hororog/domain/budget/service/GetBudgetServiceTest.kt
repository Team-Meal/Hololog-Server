package team.nongchun.hororog.domain.budget.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import team.nongchun.hororog.domain.budget.entity.Budget
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class GetBudgetServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val budgetRepository = mockk<BudgetRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = GetBudgetServiceImpl(budgetRepository, memberRepository, authenticationHolder)

        val member =
            Member(
                id = 1L,
                email = "nutritionist@hororog.team",
                password = "encoded",
                name = "김영양",
                schoolName = "농촌초등학교",
                role = Role.NUTRITIONIST,
            )
        val budget =
            Budget(
                id = 10L,
                member = member,
                name = "6월 급식 예산",
                totalBudget = 1_000_000,
                usedBudget = 100_000,
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
            )

        Given("같은 학교 예산이 존재할 때") {
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { budgetRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns budget

            When("단건 조회하면") {
                val response = service.execute(10L)

                Then("명세 필드명으로 예산을 반환한다") {
                    response.id shouldBe 10L
                    response.title shouldBe "6월 급식 예산"
                    response.totalAmount shouldBe 1_000_000
                    response.usedAmount shouldBe 100_000
                    verify(exactly = 1) { budgetRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") }
                }
            }
        }

        Given("같은 학교 예산이 없을 때") {
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { budgetRepository.findByIdAndMemberSchoolName(10L, "농촌초등학교") } returns null

            When("단건 조회하면") {
                Then("BudgetNotFoundException이 발생한다") {
                    shouldThrow<BudgetNotFoundException> { service.execute(10L) }
                }
            }
        }
    })

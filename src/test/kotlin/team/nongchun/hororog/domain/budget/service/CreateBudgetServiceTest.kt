package team.nongchun.hororog.domain.budget.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import team.nongchun.hororog.domain.budget.dto.CreateBudgetRequest
import team.nongchun.hororog.domain.budget.entity.Budget
import team.nongchun.hororog.domain.budget.exception.InvalidBudgetException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.util.Optional

class CreateBudgetServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val budgetRepository = mockk<BudgetRepository>()
        val memberRepository = mockk<MemberRepository>()
        val authenticationHolder = mockk<AuthenticationHolder>()
        val service = CreateBudgetServiceImpl(budgetRepository, memberRepository, authenticationHolder)

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
            CreateBudgetRequest(
                title = "6월 급식 예산",
                totalAmount = 1_000_000,
                usedAmount = 100_000,
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 30),
            )

        Given("유효한 예산 입력 요청이 주어졌을 때") {
            val budgetSlot = slot<Budget>()
            every { authenticationHolder.getCurrentUserId() } returns member.id
            every { memberRepository.findById(member.id) } returns Optional.of(member)
            every { budgetRepository.save(capture(budgetSlot)) } answers { budgetSlot.captured }

            When("예산을 생성하면") {
                service.execute(request)

                Then("현재 회원에게 연결된 예산을 저장한다") {
                    budgetSlot.captured.member shouldBe member
                    budgetSlot.captured.name shouldBe "6월 급식 예산"
                    budgetSlot.captured.totalBudget shouldBe 1_000_000
                    budgetSlot.captured.usedBudget shouldBe 100_000
                    budgetSlot.captured.startDate shouldBe LocalDate.of(2026, 6, 1)
                    budgetSlot.captured.endDate shouldBe LocalDate.of(2026, 6, 30)
                    verify(exactly = 1) { budgetRepository.save(any()) }
                }
            }
        }

        Given("사용 금액이 전체 예산보다 큰 요청이 주어졌을 때") {
            val invalidRequest = request.copy(usedAmount = 1_000_001)

            When("예산을 생성하면") {
                Then("InvalidBudgetException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidBudgetException> { service.execute(invalidRequest) }
                    verify(exactly = 0) { budgetRepository.save(any()) }
                }
            }
        }

        Given("시작일이 종료일보다 늦은 요청이 주어졌을 때") {
            val invalidRequest =
                request.copy(
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2026, 6, 30),
                )

            When("예산을 생성하면") {
                Then("InvalidBudgetException이 발생하고 저장하지 않는다") {
                    shouldThrow<InvalidBudgetException> { service.execute(invalidRequest) }
                    verify(exactly = 0) { budgetRepository.save(any()) }
                }
            }
        }
    })

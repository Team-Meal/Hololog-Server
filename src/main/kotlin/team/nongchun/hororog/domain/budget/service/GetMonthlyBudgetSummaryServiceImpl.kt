package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.dto.BudgetMonthlySummaryResponse
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.YearMonth

@Service
@Transactional(readOnly = true)
class GetMonthlyBudgetSummaryServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetMonthlyBudgetSummaryService {
    override fun execute(month: String): BudgetMonthlySummaryResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val yearMonth = YearMonth.parse(month)
        val budgets =
            budgetRepository.findAllByMemberSchoolNameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                schoolName = schoolName,
                monthEnd = yearMonth.atEndOfMonth(),
                monthStart = yearMonth.atDay(1),
            )
        val totalBudget = budgets.sumOf { it.totalBudget }
        val usedBudget = budgets.sumOf { it.usedBudget }
        return BudgetMonthlySummaryResponse(
            month = month,
            totalBudget = totalBudget,
            usedBudget = usedBudget,
            remaining = totalBudget - usedBudget,
        )
    }
}

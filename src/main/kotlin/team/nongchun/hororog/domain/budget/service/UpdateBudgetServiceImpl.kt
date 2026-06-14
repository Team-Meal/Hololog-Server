package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.dto.BudgetUpdateResponse
import team.nongchun.hororog.domain.budget.dto.UpdateBudgetRequest
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateBudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateBudgetService {
    override fun execute(
        budgetId: Long,
        request: UpdateBudgetRequest,
    ): BudgetUpdateResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val budget =
            budgetRepository.findByIdAndMemberSchoolName(budgetId, schoolName)
                ?: throw BudgetNotFoundException()

        val title = request.title ?: budget.name
        val totalAmount = request.totalAmount ?: budget.totalBudget
        val usedAmount = request.usedAmount ?: budget.usedBudget
        val startDate = request.startDate ?: budget.startDate
        val endDate = request.endDate ?: budget.endDate

        BudgetValidator.validate(title, totalAmount, usedAmount, startDate, endDate)

        budget.name = title
        budget.totalBudget = totalAmount
        budget.usedBudget = usedAmount
        budget.startDate = startDate
        budget.endDate = endDate

        return BudgetUpdateResponse.from(budgetRepository.saveAndFlush(budget))
    }
}

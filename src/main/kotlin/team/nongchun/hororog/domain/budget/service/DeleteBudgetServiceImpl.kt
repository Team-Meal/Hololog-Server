package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class DeleteBudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : DeleteBudgetService {
    override fun execute(budgetId: Long) {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val budget =
            budgetRepository.findByIdAndMemberSchoolName(budgetId, schoolName)
                ?: throw BudgetNotFoundException()
        budgetRepository.delete(budget)
    }
}

package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.dto.BudgetResponse
import team.nongchun.hororog.domain.budget.exception.BudgetNotFoundException
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetBudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetBudgetService {
    override fun execute(budgetId: Long): BudgetResponse {
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val budget =
            budgetRepository.findByIdAndMemberSchoolName(budgetId, schoolName)
                ?: throw BudgetNotFoundException()
        return BudgetResponse.from(budget)
    }
}

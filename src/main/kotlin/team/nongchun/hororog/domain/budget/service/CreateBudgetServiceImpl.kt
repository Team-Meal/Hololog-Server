package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.dto.CreateBudgetRequest
import team.nongchun.hororog.domain.budget.entity.Budget
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateBudgetServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateBudgetService {
    override fun execute(request: CreateBudgetRequest) {
        BudgetValidator.validate(
            title = request.title,
            totalAmount = request.totalAmount,
            usedAmount = request.usedAmount,
            startDate = request.startDate,
            endDate = request.endDate,
        )
        val member =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }

        budgetRepository.save(
            Budget(
                member = member,
                name = request.title,
                totalBudget = request.totalAmount,
                usedBudget = request.usedAmount,
                startDate = request.startDate,
                endDate = request.endDate,
            ),
        )
    }
}

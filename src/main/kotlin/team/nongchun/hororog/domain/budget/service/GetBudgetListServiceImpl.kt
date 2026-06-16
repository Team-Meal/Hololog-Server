package team.nongchun.hororog.domain.budget.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.budget.dto.BudgetListResponse
import team.nongchun.hororog.domain.budget.repository.BudgetRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetBudgetListServiceImpl(
    private val budgetRepository: BudgetRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetBudgetListService {
    override fun execute(): List<BudgetListResponse> {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        return budgetRepository
            .findAllByMemberSchoolNameOrderByIdDesc(schoolName)
            .map(BudgetListResponse::from)
    }
}

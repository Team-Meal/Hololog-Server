package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.order.dto.OrderPlanListResponse
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetOrderPlanListServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetOrderPlanListService {
    override fun execute(): List<OrderPlanListResponse> {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName

        return orderPlanRepository
            .findAllByMemberSchoolNameOrderByIdDesc(schoolName)
            .map { OrderPlanListResponse.from(it) }
    }
}

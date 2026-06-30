package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.order.dto.OrderPlanDetailResponse
import team.nongchun.hororog.domain.order.dto.OrderPlanItemResponse
import team.nongchun.hororog.domain.order.exception.OrderPlanNotFoundException
import team.nongchun.hororog.domain.order.repository.OrderPlanItemRepository
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional(readOnly = true)
class GetOrderPlanServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val orderPlanItemRepository: OrderPlanItemRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetOrderPlanService {
    override fun execute(orderPlanId: Long): OrderPlanDetailResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName

        val orderPlan =
            orderPlanRepository.findByIdAndMemberSchoolName(orderPlanId, schoolName)
                ?: throw OrderPlanNotFoundException()

        val items =
            orderPlanItemRepository
                .findAllByOrderPlanId(orderPlanId)
                .map { OrderPlanItemResponse.from(it) }

        return OrderPlanDetailResponse.from(orderPlan, items)
    }
}

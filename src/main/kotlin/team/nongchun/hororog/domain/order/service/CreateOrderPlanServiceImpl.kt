package team.nongchun.hororog.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.order.dto.CreateOrderPlanRequest
import team.nongchun.hororog.domain.order.dto.OrderPlanDetailResponse
import team.nongchun.hororog.domain.order.entity.OrderPlan
import team.nongchun.hororog.domain.order.repository.OrderPlanRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class CreateOrderPlanServiceImpl(
    private val orderPlanRepository: OrderPlanRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateOrderPlanService {
    override fun execute(request: CreateOrderPlanRequest): OrderPlanDetailResponse {
        val member = memberRepository.getReferenceById(authenticationHolder.getCurrentUserId())

        val orderPlan =
            orderPlanRepository.save(
                OrderPlan(
                    member = member,
                    title = request.title,
                    planDate = request.planDate,
                    studentCount = request.studentCount,
                    memo = request.memo,
                ),
            )

        return OrderPlanDetailResponse.from(orderPlan, emptyList())
    }
}

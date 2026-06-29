package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

// AI 서버는 식단 슬롯(날짜+끼니) 생성 후 별도 호출로 메뉴/영양정보를 채우는 2단계 프로토콜을 쓴다.
// name은 그 두 번째 호출(CreateAiMealService)에서 채워지기 전까지 비어 있다.
@Service
@Transactional
class CreateAiMealSlotServiceImpl(
    private val mealRepository: MealRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : CreateAiMealSlotService {
    override fun execute(request: CreateAiMealSlotRequest): Long {
        val userId = authenticationHolder.getCurrentUserId()
        if (request.schoolId != userId) throw MemberNotFoundException()
        val member =
            memberRepository
                .findById(userId)
                .orElseThrow { MemberNotFoundException() }
        val meal =
            mealRepository.save(
                Meal(
                    member = member,
                    name = "",
                    mealType = request.mealType,
                    mealDate = request.date.atStartOfDay(),
                ),
            )
        return meal.id
    }
}

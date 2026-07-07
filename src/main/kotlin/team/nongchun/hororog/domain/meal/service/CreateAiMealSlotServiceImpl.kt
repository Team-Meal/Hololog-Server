package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.CreateAiMealSlotRequest
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository

// AI 서버는 식단 슬롯(날짜+끼니) 생성 후 별도 호출로 메뉴/영양정보를 채우는 2단계 프로토콜을 쓴다.
// name은 그 두 번째 호출(CreateAiMealService)에서 채워지기 전까지 비어 있다.
// ponytail: JSON 필드는 school_id지만 현재 AI 계약상 회원 ID다. School 엔티티가 생기면 이 계약부터 바꾼다.
@Service
@Transactional
class CreateAiMealSlotServiceImpl(
    private val mealRepository: MealRepository,
    private val memberRepository: MemberRepository,
) : CreateAiMealSlotService {
    override fun execute(request: CreateAiMealSlotRequest): Long {
        val member =
            memberRepository
                .findById(request.schoolId)
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

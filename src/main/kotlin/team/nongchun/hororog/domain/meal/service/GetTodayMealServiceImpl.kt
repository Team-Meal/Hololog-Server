package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.TodayMealResponse
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class GetTodayMealServiceImpl(
    private val mealRepository: MealRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetTodayMealService {
    override fun execute(mealType: MealType): TodayMealResponse {
        val today = LocalDate.now()
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName
        val meals =
            mealRepository.findAllByMemberSchoolNameAndMealTypeAndMealDateBetweenOrderByIdAsc(
                schoolName = schoolName,
                mealType = mealType,
                startDateTime = today.atStartOfDay(),
                endDateTime = today.plusDays(1).atStartOfDay().minusNanos(1),
            )
        return TodayMealResponse.of(today, mealType, meals)
    }
}

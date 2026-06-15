package team.nongchun.hororog.domain.meal.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.meal.dto.TodayMealResponse
import team.nongchun.hororog.domain.meal.entity.MealType
import team.nongchun.hororog.domain.meal.repository.MealRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class GetTodayMealServiceImpl(
    private val mealRepository: MealRepository,
    private val authenticationHolder: AuthenticationHolder,
) : GetTodayMealService {
    override fun execute(mealType: MealType): TodayMealResponse {
        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val schoolName = authenticationHolder.getCurrentUserSchoolName()
        val meals =
            mealRepository.findAllByMemberSchoolNameAndMealTypeAndMealDateGreaterThanEqualAndMealDateLessThanOrderByIdAsc(
                schoolName = schoolName,
                mealType = mealType,
                startDateTime = today.atStartOfDay(),
                endDateTime = today.plusDays(1).atStartOfDay(),
            )
        return TodayMealResponse.of(today, mealType, meals)
    }
}

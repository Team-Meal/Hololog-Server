package team.nongchun.hororog.domain.meal.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.meal.entity.Meal
import team.nongchun.hororog.domain.meal.entity.MealType
import java.time.LocalDateTime

interface MealRepository : JpaRepository<Meal, Long> {
    fun findAllByMemberSchoolNameAndMealTypeAndMealDateBetweenOrderByIdAsc(
        schoolName: String,
        mealType: MealType,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): List<Meal>
}

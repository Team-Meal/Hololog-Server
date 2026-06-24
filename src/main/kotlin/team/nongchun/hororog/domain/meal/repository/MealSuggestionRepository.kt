package team.nongchun.hororog.domain.meal.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.meal.entity.MealSuggestion

interface MealSuggestionRepository : JpaRepository<MealSuggestion, Long> {
    fun findAllByMemberSchoolNameOrderByIdDesc(schoolName: String): List<MealSuggestion>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): MealSuggestion?
}

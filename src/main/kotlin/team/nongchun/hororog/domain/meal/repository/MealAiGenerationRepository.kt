package team.nongchun.hororog.domain.meal.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.meal.entity.MealAiGeneration
import team.nongchun.hororog.domain.meal.entity.MealAiGenerationStatus

interface MealAiGenerationRepository : JpaRepository<MealAiGeneration, Long> {
    fun findBySchoolNameAndMonthAndStatusIn(
        schoolName: String,
        month: String,
        statuses: List<MealAiGenerationStatus>,
    ): MealAiGeneration?

    fun findBySchoolNameAndMonthAndStatus(
        schoolName: String,
        month: String,
        status: MealAiGenerationStatus,
    ): MealAiGeneration?
}

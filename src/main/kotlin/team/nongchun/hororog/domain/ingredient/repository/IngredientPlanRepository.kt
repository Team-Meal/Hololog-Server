package team.nongchun.hororog.domain.ingredient.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.ingredient.entity.IngredientPlan

interface IngredientPlanRepository : JpaRepository<IngredientPlan, Long> {
    fun findAllByMemberSchoolName(schoolName: String): List<IngredientPlan>
}

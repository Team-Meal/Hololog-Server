package team.nongchun.hororog.domain.ingredient.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.ingredient.entity.Ingredient

interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findAllByMemberSchoolName(schoolName: String): List<Ingredient>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): Ingredient?
}

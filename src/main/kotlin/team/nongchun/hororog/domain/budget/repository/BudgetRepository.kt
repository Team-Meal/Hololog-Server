package team.nongchun.hororog.domain.budget.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.budget.entity.Budget

interface BudgetRepository : JpaRepository<Budget, Long> {
    fun findAllByMemberSchoolName(schoolName: String): List<Budget>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): Budget?
}

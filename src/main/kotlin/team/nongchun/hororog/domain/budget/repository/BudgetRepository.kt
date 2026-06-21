package team.nongchun.hororog.domain.budget.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.budget.entity.Budget
import java.time.LocalDate

interface BudgetRepository : JpaRepository<Budget, Long> {
    fun findAllByMemberSchoolNameOrderByIdDesc(schoolName: String): List<Budget>

    fun findByIdAndMemberSchoolName(
        id: Long,
        schoolName: String,
    ): Budget?

    fun findAllByMemberSchoolNameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        schoolName: String,
        monthEnd: LocalDate,
        monthStart: LocalDate,
    ): List<Budget>
}

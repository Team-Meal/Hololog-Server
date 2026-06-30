package team.nongchun.hororog.domain.budget.service

import team.nongchun.hororog.domain.budget.exception.InvalidBudgetException
import java.time.LocalDate

object BudgetValidator {
    fun validate(
        title: String,
        totalAmount: Long,
        usedAmount: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        if (title.isBlank()) {
            throw InvalidBudgetException("예산 제목은 비어 있을 수 없습니다.")
        }
        if (usedAmount > totalAmount) {
            throw InvalidBudgetException("사용 금액은 전체 예산을 초과할 수 없습니다.")
        }
        if (startDate.isAfter(endDate)) {
            throw InvalidBudgetException("시작일은 종료일보다 늦을 수 없습니다.")
        }
    }
}

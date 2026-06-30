package team.nongchun.hororog.domain.ingredient.service

import team.nongchun.hororog.domain.ingredient.exception.InvalidIngredientPlanException
import java.time.LocalDate

object IngredientPlanValidator {
    fun validate(
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        if (startDate.isAfter(endDate)) {
            throw InvalidIngredientPlanException("시작일은 종료일보다 늦을 수 없습니다.")
        }
    }
}

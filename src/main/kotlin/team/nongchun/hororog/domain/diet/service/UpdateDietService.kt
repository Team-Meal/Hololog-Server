package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.DietResponse
import team.nongchun.hororog.domain.diet.dto.UpdateDietRequest

interface UpdateDietService {
    fun execute(
        dietId: Long,
        request: UpdateDietRequest,
    ): DietResponse
}

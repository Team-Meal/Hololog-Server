package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.CreateDietLeftoverRequest

interface CreateDietLeftoverService {
    fun execute(
        dietId: Long,
        request: CreateDietLeftoverRequest,
    )
}

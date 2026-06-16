package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.DietLeftoverResponse

interface GetDietLeftoverService {
    fun execute(dietId: Long): DietLeftoverResponse
}

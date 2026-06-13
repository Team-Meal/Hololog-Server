package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.DietResponse

interface GetDietService {
    fun execute(dietId: Long): DietResponse
}

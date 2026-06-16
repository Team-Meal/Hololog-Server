package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.DietListResponse

interface GetDietListService {
    fun execute(): List<DietListResponse>
}

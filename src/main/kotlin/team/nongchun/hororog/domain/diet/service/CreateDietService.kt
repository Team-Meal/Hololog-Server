package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.CreateDietRequest

interface CreateDietService {
    fun execute(request: CreateDietRequest)
}

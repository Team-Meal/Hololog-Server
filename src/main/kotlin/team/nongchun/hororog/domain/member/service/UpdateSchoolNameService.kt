package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.ProfileResponse
import team.nongchun.hororog.domain.member.dto.UpdateSchoolNameRequest

interface UpdateSchoolNameService {
    fun execute(request: UpdateSchoolNameRequest): ProfileResponse
}

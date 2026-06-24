package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.ProfileResponse

interface GetProfileService {
    fun execute(): ProfileResponse
}

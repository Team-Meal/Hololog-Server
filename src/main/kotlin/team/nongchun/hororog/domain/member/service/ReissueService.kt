package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.SigninResponse

interface ReissueService {
    fun execute(refreshToken: String): SigninResponse
}

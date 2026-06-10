package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse

interface SigninService {
    fun execute(request: SigninRequest): SigninResponse
}

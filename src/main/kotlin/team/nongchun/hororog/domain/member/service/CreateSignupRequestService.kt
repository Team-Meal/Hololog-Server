package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.CreateSignupRequestRequest
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse

interface CreateSignupRequestService {
    fun execute(request: CreateSignupRequestRequest): SignupRequestResponse
}

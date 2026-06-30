package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.CreateSignupRequest
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse

interface CreateSignupRequestService {
    fun execute(request: CreateSignupRequest): SignupRequestResponse
}

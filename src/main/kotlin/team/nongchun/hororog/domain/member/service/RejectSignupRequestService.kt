package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.SignupRequestResponse

interface RejectSignupRequestService {
    fun execute(requestId: Long): SignupRequestResponse
}

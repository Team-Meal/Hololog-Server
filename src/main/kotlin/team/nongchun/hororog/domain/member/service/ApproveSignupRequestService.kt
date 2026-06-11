package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.SignupRequestResponse

interface ApproveSignupRequestService {
    fun execute(requestId: Long): SignupRequestResponse
}

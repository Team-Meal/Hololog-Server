package team.nongchun.hororog.domain.member.service

import team.nongchun.hororog.domain.member.dto.SignupRequest

interface SignupService {
    fun execute(request: SignupRequest)
}

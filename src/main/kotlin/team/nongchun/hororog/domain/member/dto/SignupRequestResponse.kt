package team.nongchun.hororog.domain.member.dto

import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.SignupStatus

data class SignupRequestResponse(
    val requestId: Long,
    val licenseNumber: String,
    val status: SignupStatus,
) {
    companion object {
        fun from(entity: NutritionistSignupRequest) =
            SignupRequestResponse(
                requestId = entity.id,
                licenseNumber = entity.licenseNumber,
                status = entity.status,
            )
    }
}

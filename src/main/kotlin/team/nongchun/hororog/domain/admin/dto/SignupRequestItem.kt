package team.nongchun.hororog.domain.admin.dto

import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest
import team.nongchun.hororog.domain.member.entity.SignupStatus

data class SignupRequestItem(
    val requestId: Long,
    val memberId: Long,
    val name: String,
    val licenseNumber: String,
    val status: SignupStatus,
) {
    companion object {
        fun from(entity: NutritionistSignupRequest) =
            SignupRequestItem(
                requestId = entity.id,
                memberId = entity.member.id,
                name = entity.member.name,
                licenseNumber = entity.licenseNumber,
                status = entity.status,
            )
    }
}

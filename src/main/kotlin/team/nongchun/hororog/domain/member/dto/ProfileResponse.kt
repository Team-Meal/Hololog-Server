package team.nongchun.hororog.domain.member.dto

import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role

data class ProfileResponse(
    val name: String,
    val role: Role,
    val schoolName: String,
) {
    companion object {
        fun from(entity: Member) =
            ProfileResponse(
                name = entity.name,
                role = entity.role,
                schoolName = entity.schoolName,
            )
    }
}

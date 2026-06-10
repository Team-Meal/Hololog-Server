package team.nongchun.hororog.domain.member.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSignupRequestRequest(
    @JsonProperty("license_number")
    val licenseNumber: Long,
)

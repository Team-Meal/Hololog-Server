package team.nongchun.hororog.domain.meal.client.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AiGeneratePlanRequest(
    val month: String,
    @JsonProperty("school_id")
    val schoolId: Long,
    val holidays: List<String> = emptyList(),
)

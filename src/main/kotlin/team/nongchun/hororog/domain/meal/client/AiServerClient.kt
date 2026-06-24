package team.nongchun.hororog.domain.meal.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanRequest
import team.nongchun.hororog.domain.meal.client.dto.AiGeneratePlanResponse

@FeignClient(name = "ai-server", url = "\${ai.server.url}")
interface AiServerClient {
    @PostMapping("/agent/generate-plan")
    fun generatePlan(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: AiGeneratePlanRequest,
    ): AiGeneratePlanResponse
}

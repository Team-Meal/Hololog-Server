package team.nongchun.hororog.domain.member.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.CreateSignupRequestRequest
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse
import team.nongchun.hororog.domain.member.service.CreateSignupRequestService

@RestController
@RequestMapping("/signup-requests")
class SignupRequestController(
    private val createSignupRequestService: CreateSignupRequestService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateSignupRequestRequest,
    ): SignupRequestResponse = createSignupRequestService.execute(request)
}

package team.nongchun.hororog.domain.member.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.CreateSignupRequestRequest
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.dto.SignupRequest
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse
import team.nongchun.hororog.domain.member.service.ApproveSignupRequestService
import team.nongchun.hororog.domain.member.service.CreateSignupRequestService
import team.nongchun.hororog.domain.member.service.LogoutService
import team.nongchun.hororog.domain.member.service.ReissueService
import team.nongchun.hororog.domain.member.service.RejectSignupRequestService
import team.nongchun.hororog.domain.member.service.SigninService
import team.nongchun.hororog.domain.member.service.SignupService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val signupService: SignupService,
    private val signinService: SigninService,
    private val reissueService: ReissueService,
    private val logoutService: LogoutService,
    private val createSignupRequestService: CreateSignupRequestService,
    private val approveSignupRequestService: ApproveSignupRequestService,
    private val rejectSignupRequestService: RejectSignupRequestService,
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ) {
        signupService.execute(request)
    }

    @PostMapping("/signin")
    fun signin(
        @Valid @RequestBody request: SigninRequest,
    ): SigninResponse = signinService.execute(request)

    @PostMapping("/reissue")
    fun reissue(
        @RequestHeader("Refresh-Token") refreshToken: String,
    ): SigninResponse = reissueService.execute(refreshToken)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout() = logoutService.execute()

    @PostMapping("/signup-requests")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSignupRequest(
        @Valid @RequestBody request: CreateSignupRequestRequest,
    ): SignupRequestResponse = createSignupRequestService.execute(request)

    @PostMapping("/signup-requests/{requestId}/approve")
    fun approveSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = approveSignupRequestService.execute(requestId)

    @PostMapping("/signup-requests/{requestId}/reject")
    fun rejectSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = rejectSignupRequestService.execute(requestId)
}

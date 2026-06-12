package team.nongchun.hororog.domain.member.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.dto.SignupRequest
import team.nongchun.hororog.domain.member.service.LogoutService
import team.nongchun.hororog.domain.member.service.ReissueService
import team.nongchun.hororog.domain.member.service.SigninService
import team.nongchun.hororog.domain.member.service.SignupService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val signupService: SignupService,
    private val signinService: SigninService,
    private val reissueService: ReissueService,
    private val logoutService: LogoutService,
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
}

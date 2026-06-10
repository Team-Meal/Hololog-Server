package team.nongchun.hororog.domain.member.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.SignupRequest
import team.nongchun.hororog.domain.member.service.SignupService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val signupService: SignupService,
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ) {
        signupService.execute(request)
    }
}

package team.nongchun.hororog.domain.member.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.SigninRequest
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.service.SigninService

@RestController
class SigninController(
    private val signinService: SigninService,
) {
    @PostMapping("/auth/signin")
    fun signin(
        @Valid @RequestBody request: SigninRequest,
    ): SigninResponse = signinService.execute(request)
}

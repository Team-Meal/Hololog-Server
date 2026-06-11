package team.nongchun.hororog.domain.member.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.SigninResponse
import team.nongchun.hororog.domain.member.service.LogoutService
import team.nongchun.hororog.domain.member.service.ReissueService

@RestController
@RequestMapping("/auth")
class TokenController(
    private val reissueService: ReissueService,
    private val logoutService: LogoutService,
) {
    @PostMapping("/reissue")
    fun reissue(
        @RequestHeader("Refresh-Token") refreshToken: String,
    ): SigninResponse = reissueService.execute(refreshToken)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout() = logoutService.execute()
}

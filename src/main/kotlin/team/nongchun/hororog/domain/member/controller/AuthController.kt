package team.nongchun.hororog.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
import team.nongchun.hororog.domain.member.service.CreateSignupRequestService
import team.nongchun.hororog.domain.member.service.LogoutService
import team.nongchun.hororog.domain.member.service.ReissueService
import team.nongchun.hororog.domain.member.service.SigninService
import team.nongchun.hororog.domain.member.service.SignupService

@Tag(name = "Auth", description = "인증/인가 API")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val signupService: SignupService,
    private val signinService: SigninService,
    private val reissueService: ReissueService,
    private val logoutService: LogoutService,
    private val createSignupRequestService: CreateSignupRequestService,
) {
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 학교명으로 회원가입합니다. 가입 후 역할은 PENDING_NUTRITIONIST입니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "가입 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일"),
    )
    @SecurityRequirements
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ) {
        signupService.execute(request)
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "로그인 성공"),
        ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
    )
    @SecurityRequirements
    @PostMapping("/signin")
    fun signin(
        @Valid @RequestBody request: SigninRequest,
    ): SigninResponse = signinService.execute(request)

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token과 Refresh Token을 발급합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "재발급 성공"),
        ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 Refresh Token"),
    )
    @SecurityRequirements
    @PostMapping("/reissue")
    fun reissue(
        @RequestHeader("Refresh-Token") refreshToken: String,
    ): SigninResponse = reissueService.execute(refreshToken)

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하여 로그아웃합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "로그아웃 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
    )
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout() = logoutService.execute()

    @Operation(summary = "영양사 가입 요청 제출", description = "면허번호를 제출하여 영양사 가입 승인을 요청합니다. PENDING_NUTRITIONIST 역할만 접근 가능합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "요청 제출 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "403", description = "권한 없음 (PENDING_NUTRITIONIST만 접근 가능)"),
        ApiResponse(responseCode = "409", description = "이미 처리 대기 중인 요청 존재"),
    )
    @PostMapping("/signup-requests")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSignupRequest(
        @Valid @RequestBody request: CreateSignupRequestRequest,
    ): SignupRequestResponse = createSignupRequestService.execute(request)
}

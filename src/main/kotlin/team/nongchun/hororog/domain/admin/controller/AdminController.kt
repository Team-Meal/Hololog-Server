package team.nongchun.hororog.domain.admin.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.admin.dto.SignupRequestItem
import team.nongchun.hororog.domain.admin.service.GetSignupRequestListService
import team.nongchun.hororog.domain.member.dto.SignupRequestResponse
import team.nongchun.hororog.domain.member.service.ApproveSignupRequestService
import team.nongchun.hororog.domain.member.service.RejectSignupRequestService

@Tag(name = "Admin", description = "어드민 전용 API")
@RestController
@RequestMapping("/admin")
class AdminController(
    private val getSignupRequestListService: GetSignupRequestListService,
    private val approveSignupRequestService: ApproveSignupRequestService,
    private val rejectSignupRequestService: RejectSignupRequestService,
) {
    @Operation(summary = "회원가입 요청 목록 조회", description = "PENDING 상태의 영양사 회원가입 요청 목록을 페이징으로 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)"),
    )
    @GetMapping("/signup-requests")
    fun getSignupRequestList(pageable: Pageable): Page<SignupRequestItem> = getSignupRequestListService.execute(pageable)

    @Operation(summary = "회원가입 요청 승인", description = "영양사 회원가입 요청을 승인하고 회원 역할을 NUTRITIONIST로 전환합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "승인 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)"),
        ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음"),
        ApiResponse(responseCode = "409", description = "이미 처리된 요청"),
    )
    @PostMapping("/signup-requests/{requestId}/approve")
    fun approveSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = approveSignupRequestService.execute(requestId)

    @Operation(summary = "회원가입 요청 거절", description = "영양사 회원가입 요청을 거절합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "거절 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)"),
        ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음"),
        ApiResponse(responseCode = "409", description = "이미 처리된 요청"),
    )
    @PostMapping("/signup-requests/{requestId}/reject")
    fun rejectSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = rejectSignupRequestService.execute(requestId)
}

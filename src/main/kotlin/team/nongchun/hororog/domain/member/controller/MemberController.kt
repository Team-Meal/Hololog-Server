package team.nongchun.hororog.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.nongchun.hororog.domain.member.dto.ProfileResponse
import team.nongchun.hororog.domain.member.dto.UpdateSchoolNameRequest
import team.nongchun.hororog.domain.member.service.GetProfileService
import team.nongchun.hororog.domain.member.service.UpdateSchoolNameService

@Tag(name = "Member", description = "회원 프로필 API")
@RestController
@RequestMapping("/members")
class MemberController(
    private val getProfileService: GetProfileService,
    private val updateSchoolNameService: UpdateSchoolNameService,
) {
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 회원의 이름, 권한, 소속 학교를 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
    )
    @GetMapping("/me")
    fun getProfile(): ProfileResponse = getProfileService.execute()

    @Operation(summary = "소속 학교 수정", description = "현재 로그인한 회원의 소속 학교명을 수정합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
        ApiResponse(responseCode = "401", description = "인증 실패"),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
    )
    @PatchMapping("/me/school-name")
    fun updateSchoolName(
        @Valid @RequestBody request: UpdateSchoolNameRequest,
    ): ProfileResponse = updateSchoolNameService.execute(request)
}

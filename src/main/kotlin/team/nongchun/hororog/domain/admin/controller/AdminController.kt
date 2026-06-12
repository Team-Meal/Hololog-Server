package team.nongchun.hororog.domain.admin.controller

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

@RestController
@RequestMapping("/admin")
class AdminController(
    private val getSignupRequestListService: GetSignupRequestListService,
    private val approveSignupRequestService: ApproveSignupRequestService,
    private val rejectSignupRequestService: RejectSignupRequestService,
) {
    @GetMapping("/signup-requests")
    fun getSignupRequestList(pageable: Pageable): Page<SignupRequestItem> = getSignupRequestListService.execute(pageable)

    @PostMapping("/signup-requests/{requestId}/approve")
    fun approveSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = approveSignupRequestService.execute(requestId)

    @PostMapping("/signup-requests/{requestId}/reject")
    fun rejectSignupRequest(
        @PathVariable requestId: Long,
    ): SignupRequestResponse = rejectSignupRequestService.execute(requestId)
}

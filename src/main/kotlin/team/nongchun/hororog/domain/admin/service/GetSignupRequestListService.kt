package team.nongchun.hororog.domain.admin.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import team.nongchun.hororog.domain.admin.dto.SignupRequestItem

interface GetSignupRequestListService {
    fun execute(pageable: Pageable): Page<SignupRequestItem>
}

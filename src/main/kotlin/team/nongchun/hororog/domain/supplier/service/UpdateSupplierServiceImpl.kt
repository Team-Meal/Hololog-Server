package team.nongchun.hororog.domain.supplier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.supplier.dto.SupplierResponse
import team.nongchun.hororog.domain.supplier.dto.UpdateSupplierRequest
import team.nongchun.hororog.domain.supplier.exception.SupplierNotFoundException
import team.nongchun.hororog.domain.supplier.repository.SupplierRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class UpdateSupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : UpdateSupplierService {
    override fun execute(
        supplierId: Long,
        request: UpdateSupplierRequest,
    ): SupplierResponse {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName

        val supplier =
            supplierRepository.findByIdAndMemberSchoolName(supplierId, schoolName)
                ?: throw SupplierNotFoundException()

        request.name?.let { supplier.name = it }
        request.contactInfo?.let { supplier.contactInfo = it }

        return SupplierResponse.from(supplierRepository.saveAndFlush(supplier))
    }
}

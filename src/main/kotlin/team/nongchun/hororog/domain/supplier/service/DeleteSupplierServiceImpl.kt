package team.nongchun.hororog.domain.supplier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.member.exception.MemberNotFoundException
import team.nongchun.hororog.domain.member.repository.MemberRepository
import team.nongchun.hororog.domain.supplier.exception.SupplierNotFoundException
import team.nongchun.hororog.domain.supplier.repository.SupplierRepository
import team.nongchun.hororog.global.auth.AuthenticationHolder

@Service
@Transactional
class DeleteSupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val memberRepository: MemberRepository,
    private val authenticationHolder: AuthenticationHolder,
) : DeleteSupplierService {
    override fun execute(supplierId: Long) {
        val schoolName =
            memberRepository
                .findById(authenticationHolder.getCurrentUserId())
                .orElseThrow { MemberNotFoundException() }
                .schoolName

        val supplier =
            supplierRepository.findByIdAndMemberSchoolName(supplierId, schoolName)
                ?: throw SupplierNotFoundException()

        supplierRepository.delete(supplier)
    }
}

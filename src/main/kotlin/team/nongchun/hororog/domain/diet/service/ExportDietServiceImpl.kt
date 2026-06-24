package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.DietExportResponse
import team.nongchun.hororog.domain.diet.dto.ExportDietRequest
import team.nongchun.hororog.domain.diet.entity.DietExportFormat
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository

@Service
@Transactional(readOnly = true)
class ExportDietServiceImpl(
    private val dietRepository: DietRepository,
) : ExportDietService {
    override fun execute(
        dietId: Long,
        request: ExportDietRequest,
    ): DietExportResponse {
        val diet = dietRepository.findById(dietId).orElseThrow { DietNotFoundException() }
        val fileUrl = generateFileUrl(diet.id, request.dietExportFormat)
        return DietExportResponse(
            id = diet.id,
            dietExportFormat = request.dietExportFormat,
            fileUrl = fileUrl,
        )
    }

    private fun generateFileUrl(
        dietId: Long,
        format: DietExportFormat,
    ): String {
        val extension =
            when (format) {
                DietExportFormat.PDF -> "pdf"
                DietExportFormat.EXCEL -> "xlsx"
                DietExportFormat.IMAGE -> "png"
            }
        // TODO: 실제 파일 생성 및 스토리지 업로드 로직 구현 필요
        return "/exports/diet-$dietId.$extension"
    }
}

package team.nongchun.hororog.domain.diet.service

import team.nongchun.hororog.domain.diet.dto.DietExportResponse
import team.nongchun.hororog.domain.diet.dto.ExportDietRequest

interface ExportDietService {
    fun execute(
        dietId: Long,
        request: ExportDietRequest,
    ): DietExportResponse
}

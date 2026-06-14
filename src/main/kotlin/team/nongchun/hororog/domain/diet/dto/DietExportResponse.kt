package team.nongchun.hororog.domain.diet.dto

import team.nongchun.hororog.domain.diet.entity.DietExportFormat

data class DietExportResponse(
    val id: Long,
    val dietExportFormat: DietExportFormat,
    val fileUrl: String,
)

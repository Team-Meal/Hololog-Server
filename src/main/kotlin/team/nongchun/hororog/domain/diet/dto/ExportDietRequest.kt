package team.nongchun.hororog.domain.diet.dto

import jakarta.validation.constraints.NotNull
import team.nongchun.hororog.domain.diet.entity.DietExportFormat

data class ExportDietRequest(
    @field:NotNull
    val dietExportFormat: DietExportFormat,
)

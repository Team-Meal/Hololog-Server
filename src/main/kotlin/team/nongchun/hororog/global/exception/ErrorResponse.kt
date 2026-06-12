package team.nongchun.hororog.global.exception

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val status: Int,
    val message: String,
) {
    companion object {
        fun of(
            status: HttpStatus,
            message: String,
        ) = ErrorResponse(
            status = status.value(),
            message = message,
        )
    }
}

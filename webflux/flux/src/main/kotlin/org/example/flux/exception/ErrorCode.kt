package org.example.flux.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val reason: String
) {
    QUEUE_ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "UQ-0001", "Already registered in queue");

    fun build(): ApplicationException {
        return ApplicationException(httpStatus, code, reason)
    }
}
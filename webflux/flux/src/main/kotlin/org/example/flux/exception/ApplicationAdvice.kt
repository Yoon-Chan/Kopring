package org.example.flux.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationAdvice {

    @ExceptionHandler(ApplicationException::class)
    suspend fun applicationExceptionHandler(ex: ApplicationException): ResponseEntity<ServerExceptionResponse> {
        return ResponseEntity
            .status(ex.httpStatus)
            .body(ServerExceptionResponse(ex.code, ex.reason))
    }

    data class ServerExceptionResponse(
        val code: String,
        val reason: String
    )
}
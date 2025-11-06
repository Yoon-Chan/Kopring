package org.example.flux.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@RestControllerAdvice
class ApplicationAdvice {

    @ExceptionHandler(ApplicationException::class)
    fun applicationExceptionHandler(ex: ApplicationException): Mono<ResponseEntity<ServerExceptionResponse>> {
        return Mono.just(
            ResponseEntity
                .status(ex.httpStatus)
                .body(ServerExceptionResponse(ex.code, ex.reason))
        )
    }

    data class ServerExceptionResponse(
        val code: String,
        val reason: String
    )
}
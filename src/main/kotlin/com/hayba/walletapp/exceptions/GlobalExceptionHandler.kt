package com.hayba.walletapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleUnhandledExceptions(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        problemDetail.setProperty("title", "Internal Server Error")
        problemDetail.setProperty("timestamp,", Instant.now())
        return problemDetail;
    }
}
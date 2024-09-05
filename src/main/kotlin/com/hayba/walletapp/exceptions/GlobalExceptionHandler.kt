package com.hayba.walletapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleUnhandledExceptions(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        problemDetail.setProperty("title", "Internal Server Error")
        problemDetail.setProperty("timestamp,", Instant.now())
        return problemDetail;
    }

    @ExceptionHandler(InvalidFileException::class)
    fun handleInvalidFileException(e: InvalidFileException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)
        problemDetail.setProperty("title", "Bad Request Error")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(OfferingNotFoundException::class)
    fun handleOfferingNotFoundException(e: OfferingNotFoundException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
        problemDetail.setProperty("title", "Not Found Error")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(QuoteNotFoundException::class)
    fun handleQuoteNotFoundException(e: QuoteNotFoundException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
        problemDetail.setProperty("title", "Not Found Error")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(RfqNotFoundException::class)
    fun handleRfqNotFoundException(e: RfqNotFoundException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
        problemDetail.setProperty("title", "Not Found Error")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
        problemDetail.setProperty("title", "Not Found Error")
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

}
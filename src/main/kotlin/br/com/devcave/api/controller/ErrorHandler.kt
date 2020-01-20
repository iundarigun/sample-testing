package br.com.devcave.api.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ErrorHandler {
    @ExceptionHandler(
        ConstraintViolationException::class
    )
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        webRequest: ServletWebRequest
    ) {
        webRequest.response?.sendError(HttpStatus.BAD_REQUEST.value(), exception.message)
    }
}
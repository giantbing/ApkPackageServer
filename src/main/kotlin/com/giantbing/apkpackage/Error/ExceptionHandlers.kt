package com.giantbing.apkpackage.Error

import com.giantbing.apkpackage.logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

//@RestControllerAdvice
//class ExceptionHandlers {
//
//    @ExceptionHandler(Exception::class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    fun serverExceptionHandler(ex: Exception): String {
//        logger.error(ex.message, ex)
//        return ex.message!!
//    }
//
//}
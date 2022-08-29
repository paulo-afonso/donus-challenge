package com.br.donuschallenge.exception

import com.br.donuschallenge.commons.Constants
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN, reason = Constants.INCORRECT_PASSWORD_EXCEPTION)
class IncorrectPasswordException: RuntimeException()
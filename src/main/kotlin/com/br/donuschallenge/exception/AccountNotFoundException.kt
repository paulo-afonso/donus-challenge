package com.br.donuschallenge.exception

import com.br.donuschallenge.commons.Constants
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND, reason = Constants.ACCOUNT_NOT_FOUND_EXCEPTION)
class AccountNotFoundException: RuntimeException()
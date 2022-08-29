package com.br.donuschallenge.exception

import com.br.donuschallenge.commons.Constants
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = Constants.DUPLICATED_CPF_EXCEPTION)
class DuplicatedCpfException: RuntimeException()
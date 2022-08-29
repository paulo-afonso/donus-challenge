package com.br.donuschallenge.exception

import com.br.donuschallenge.commons.Constants
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN, reason = Constants.TRANSFER_INSUFFICIENT_FUNDS_EXCEPTION)
class InsufficientTransferException: RuntimeException()
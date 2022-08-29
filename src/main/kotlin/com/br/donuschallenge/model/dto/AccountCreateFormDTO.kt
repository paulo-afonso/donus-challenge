package com.br.donuschallenge.model.dto

import javax.validation.constraints.Digits
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AccountCreateFormDTO (
        @field:NotBlank
        val name: String,
        var cpf: String,
        var password: String?
        )
package com.br.donuschallenge.model.dto

import javax.validation.constraints.NotBlank

data class AccountCreateFormDTO (
        @field:NotBlank
        val name: String,
        var cpf: String,
        var password: String?
        )
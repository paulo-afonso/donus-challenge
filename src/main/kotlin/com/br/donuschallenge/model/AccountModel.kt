package com.br.donuschallenge.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.PositiveOrZero

@Document
data class AccountModel (
    @Id
    val id: String? = null,
    val name: String,
    val cpf: String,
    @Indexed(unique = true)
    val password: String,
    @field:PositiveOrZero
    var balance: Double
        )
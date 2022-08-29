package com.br.donuschallenge.model.dto

data class AccountTransferFormDTO (
    val senderCpf: String,
    val receiverCpf: String,
    val value: Double,
    val password: String
        )
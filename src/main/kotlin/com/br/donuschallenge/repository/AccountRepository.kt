package com.br.donuschallenge.repository

import com.br.donuschallenge.model.AccountModel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: MongoRepository<AccountModel, String> {

    fun findByCpf(cpf: String): AccountModel?
}
package com.br.donuschallenge.service

import br.com.colman.simplecpfvalidator.isCpf
import com.br.donuschallenge.exception.AccountNotFoundException
import com.br.donuschallenge.exception.InvalidCpfException
import com.br.donuschallenge.model.AccountModel
import com.br.donuschallenge.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValidateCpfService(
    @Autowired
    private val accountRepository: AccountRepository,
) {
    fun getByCpf(cpf: String): AccountModel {
        return accountRepository.findByCpf(cpf) ?: throw AccountNotFoundException()
    }

    fun clearAndValidateCpf(cpf: String): String {
        if (!cpf.isCpf()) {
            throw InvalidCpfException()
        } else {
            return Regex("\\D").replace(cpf, "")
        }
    }
}
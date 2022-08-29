package com.br.donuschallenge.service

import br.com.colman.simplecpfvalidator.isCpf
import com.br.donuschallenge.exception.*
import com.br.donuschallenge.model.AccountModel
import com.br.donuschallenge.model.dto.AccountCreateFormDTO
import com.br.donuschallenge.model.dto.AccountDepositFormDTO
import com.br.donuschallenge.model.dto.AccountTransferFormDTO
import com.br.donuschallenge.model.dto.AccountTransactionResponseDTO
import com.br.donuschallenge.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountService(
    @Autowired
    private val accountRepository: AccountRepository,

    @Autowired
    private val passwordService: PasswordService
) {

    fun createAccount(accountFormDto: AccountCreateFormDTO): AccountModel {

        //If user doesn't set password, randomly generate a 6 digits one
        accountFormDto.password ?: accountFormDto.apply { password = passwordService.generateRandomPassword() }

        //Password 6 digits validation if it isn't null
        passwordService.validatePasswordLength(accountFormDto.password!!)

        //CPF validation (with '.' and '-' or not), and saving only the digits on database
        if (!accountFormDto.cpf.isCpf()) throw InvalidCpfException()
        accountFormDto.apply { cpf = clearCpfSpecialCharacters(this.cpf) }

        //Saving only registers with unique CPF
        accountRepository.findByCpf(accountFormDto.cpf)?.let {
            throw DuplicatedCpfException()
        }

        return AccountModel(
                id = null,
                name = accountFormDto.name,
                cpf = accountFormDto.cpf,
                password = accountFormDto.password!!,
                balance = 0.0
            ).also { accountRepository.save(it) }
    }



    fun getAccountById(id: String): AccountModel {
        return accountRepository.findByIdOrNull(id) ?: throw AccountNotFoundException()
    }

    fun deposit(depositFormDTO: AccountDepositFormDTO): AccountTransactionResponseDTO {
        //Searching only for CPF's digits
        val receiverCpf = clearCpfSpecialCharacters(depositFormDTO.cpf)
        val receiverAccount = getByCpf(receiverCpf)

        //Validation of depositing only positive values up to 2000.0
        if (depositFormDTO.value <= 0.0 || depositFormDTO.value > 2000.0) throw InvalidTransferException()

        receiverAccount.balance += depositFormDTO.value
        accountRepository.save(receiverAccount)

        return AccountTransactionResponseDTO(receiverName = receiverAccount.name, receivedValue = depositFormDTO.value)
    }

    fun transfer(transferFormDTO: AccountTransferFormDTO): AccountTransactionResponseDTO {

        val senderCpf = clearCpfSpecialCharacters(transferFormDTO.senderCpf)
        val receiverCpf = clearCpfSpecialCharacters(transferFormDTO.receiverCpf)

        //Validation of origin and destiny accounts
        val originAccount = getByCpf(senderCpf)
        val destinyAccount = getByCpf(receiverCpf)

        //Transfer can only occur with sender's password
        if (transferFormDTO.password != originAccount.password) throw IncorrectPasswordException()

        //Validation of transferring only positive values up to 2000.0
        if (transferFormDTO.value <= 0.0 || transferFormDTO.value > 2000.0) throw InvalidTransferException()

        //Cannot transfer value higher than account's balance
        if ((originAccount.balance).minus(transferFormDTO.value) < 0.0) throw InsufficientTransferException()

        originAccount.balance -= transferFormDTO.value
        destinyAccount.balance += transferFormDTO.value

        accountRepository.save(originAccount)
        accountRepository.save(destinyAccount)

        return AccountTransactionResponseDTO(receiverName = destinyAccount.name, receivedValue = transferFormDTO.value)
    }

    fun getByCpf(cpf: String): AccountModel = accountRepository.findByCpf(cpf) ?: throw AccountNotFoundException()

    fun clearCpfSpecialCharacters(cpf: String) = Regex("\\D").replace(cpf, "")


}
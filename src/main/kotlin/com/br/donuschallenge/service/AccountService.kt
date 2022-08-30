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
    private val passwordService: ValidatePasswordService,

    @Autowired
    private val cpfService: ValidateCpfService
) {

    fun validateRegistry(accountFormDto: AccountCreateFormDTO) {
        //If user doesn't set password, randomly generate a 6 digits one
        accountFormDto.password ?: accountFormDto.apply { password = passwordService.generateRandomPassword() }

        //Password 6 digits validation if it isn't null
        passwordService.validatePasswordLength(accountFormDto.password!!)

        //CPF validation (with '.' and '-' or not), and saving only the digits on database
        val cpf = cpfService.clearAndValidateCpf(accountFormDto.cpf)
        accountFormDto.apply { this.cpf = cpf }

        //Saving only registers with unique CPF
        accountRepository.findByCpf(accountFormDto.cpf)?.let {
            throw DuplicatedCpfException()
        }
    }

    fun createAccount(accountFormDto: AccountCreateFormDTO): AccountModel {
        validateRegistry(accountFormDto)

        return AccountModel(
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
        val receiverCpf = cpfService.clearAndValidateCpf(depositFormDTO.cpf)
        val receiverAccount = cpfService.getByCpf(receiverCpf)

        //Validation of depositing only positive values up to 2000.0
        if (depositFormDTO.value <= 0.0 || depositFormDTO.value > 2000.0) throw InvalidTransferException()

        receiverAccount.balance += depositFormDTO.value
        accountRepository.save(receiverAccount)

        return AccountTransactionResponseDTO(receiverName = receiverAccount.name, receivedValue = depositFormDTO.value)
    }

    fun validateTransfer(transferFormDTO: AccountTransferFormDTO): List<AccountModel> {
        val senderCpf = cpfService.clearAndValidateCpf(transferFormDTO.senderCpf)
        val receiverCpf = cpfService.clearAndValidateCpf(transferFormDTO.receiverCpf)

        //Validation of origin and destiny accounts existence
        val originAccount = cpfService.getByCpf(senderCpf)
        val destinyAccount = cpfService.getByCpf(receiverCpf)

        //Transfer can only occur with sender's password
        if (transferFormDTO.password != originAccount.password) throw IncorrectPasswordException()

        //Validation of transferring only positive values
        if (transferFormDTO.value <= 0.0) throw InvalidTransferException()

        //Cannot transfer value higher than account's balance
        if ((originAccount.balance).minus(transferFormDTO.value) < 0.0) throw InsufficientTransferException()

        return listOf<AccountModel>(originAccount, destinyAccount)
    }

    fun transfer(transferFormDTO: AccountTransferFormDTO): AccountTransactionResponseDTO {
        val originAccount = validateTransfer(transferFormDTO)[0]
        val destinyAccount = validateTransfer(transferFormDTO)[1]

        originAccount.balance -= transferFormDTO.value
        destinyAccount.balance += transferFormDTO.value

        accountRepository.save(originAccount)
        accountRepository.save(destinyAccount)

        return AccountTransactionResponseDTO(receiverName = destinyAccount.name, receivedValue = transferFormDTO.value)
    }
}
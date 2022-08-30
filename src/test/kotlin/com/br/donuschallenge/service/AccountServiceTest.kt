package com.br.donuschallenge.service

import com.br.donuschallenge.exception.*
import com.br.donuschallenge.model.AccountModel
import com.br.donuschallenge.model.dto.AccountCreateFormDTO
import com.br.donuschallenge.model.dto.AccountDepositFormDTO
import com.br.donuschallenge.model.dto.AccountTransferFormDTO
import com.br.donuschallenge.repository.AccountRepository
import com.ninjasquad.springmockk.clear
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AccountServiceTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var passwordService: ValidatePasswordService

    @MockK
    lateinit var cpfService: ValidateCpfService

    @InjectMockKs
    lateinit var accountService: AccountService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given I want to create a new account, When I send a name, CPF and password, Then my account should be created` () {
        val name = "Paulo"
        val cpf = "76884915331"
        val password = "123456"

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns null
        every { passwordService.generateRandomPassword() } returns password
        every { passwordService.validatePasswordLength(any()) } returns Unit
        every { cpfService.clearAndValidateCpf(cpf) } returns cpf

        val accountInfo = AccountCreateFormDTO(name, cpf, password)
        val newAccount = accountService.createAccount(accountInfo)

        assertThat(newAccount).isNotNull
        assertThat(newAccount.name).isEqualTo(name)
        assertThat(newAccount.cpf).isEqualTo(cpf)
        assertThat(newAccount.password).isEqualTo(password)
        assertThat(newAccount.balance).isEqualTo(0.0)
    }

    @Test
    fun `Given I want to create a new account, When I send a name, CPF and a invalid password, Then my account should not be created` () {
        val name = "Paulo"
        val cpf = "76884915331"
        val password = "1234567"

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns null
        every { passwordService.generateRandomPassword() } returns password
        every { passwordService.validatePasswordLength(password) } throws InvalidPasswordException()
        every { cpfService.clearAndValidateCpf(cpf) } returns cpf

        val accountInfo = AccountCreateFormDTO(name, cpf, password)

        assertThrows(InvalidPasswordException::class.java) { accountService.createAccount(accountInfo) }
    }

    @Test
    fun `Given I want to create a new account, When I send my name and CPF without password, Then my account should be created with a random password` () {
        val name = "Paulo"
        val cpf = "76884915331"

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns null
        every { passwordService.generateRandomPassword() } returns "123456"
        every { passwordService.validatePasswordLength(any()) } returns Unit
        every { cpfService.clearAndValidateCpf(cpf) } returns cpf

        val accountInfo = AccountCreateFormDTO(name, cpf, null)
        val newAccount = accountService.createAccount(accountInfo)

        assertThat(newAccount).isNotNull
        assertThat(newAccount.name).isEqualTo(name)
        assertThat(newAccount.cpf).isEqualTo(cpf)
        assertThat(newAccount.password).isNotNull
        assertThat(newAccount.password.length).isEqualTo(6)
        assertThat(newAccount.balance).isEqualTo(0.0)
    }

    @Test
    fun `Given I want to create a new account, When I send an invalid CPf, Then the account must not be created` () {
        val name = "Paulo"
        val cpf1 = "11111"
        val cpf2 = "9999999999999"

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns null
        every { passwordService.generateRandomPassword() } returns "123465"
        every { passwordService.validatePasswordLength(any()) } returns Unit
        every { cpfService.clearAndValidateCpf(cpf1) } throws InvalidCpfException()
        every { cpfService.clearAndValidateCpf(cpf2) } throws InvalidCpfException()

        val accountInfo1 = AccountCreateFormDTO(name, cpf1, "123456")
        val accountInfo2 = AccountCreateFormDTO(name, cpf2, "123456")

        assertThrows(InvalidCpfException::class.java) { accountService.createAccount(accountInfo1) }
        assertThrows(InvalidCpfException::class.java) { accountService.createAccount(accountInfo2) }
    }

    @Test
    fun `Given I want to create a new account with existing CPF, When I send my name, and duplicated CPF, Then an exception should be thrown` () {
        val name = "Paulo"
        val cpf = "768.849.153-31"

        val existingAccount = AccountModel(
            id = null,
            name = "Zé",
            cpf = "76884915331",
            password = "111111",
            balance = 0.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns existingAccount
        every { passwordService.generateRandomPassword() } returns "123456"
        every { passwordService.validatePasswordLength(any()) } returns Unit
        every { cpfService.clearAndValidateCpf(cpf) } returns cpf
        every { cpfService.getByCpf(cpf) } answers { firstArg() }

        val accountInfo = AccountCreateFormDTO(name, cpf, null)

        assertThrows(DuplicatedCpfException::class.java) { accountService.createAccount(accountInfo) }
    }

    @Test
    fun `Given I want to deposit on an account, When I send the account's owner CPF and value, Then the deposit should be made` () {
        val account = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "111111",
            balance = 0.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns account
        every { cpfService.clearAndValidateCpf(any()) } returns account.cpf
        every { cpfService.getByCpf(any()) } returns account

        val myDeposit = AccountDepositFormDTO("768.849.153-31", 100.0)
        accountService.deposit(myDeposit)

        assertThat(account.balance).isEqualTo(100.0)
    }

    @Test
    fun `Given I want to deposit to an account, When I try to send an negative value or higher than 2000, Then the deposit must fail` () {
        val account = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "121314",
            balance = 100.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns account
        every { cpfService.clearAndValidateCpf(any()) } returns account.cpf
        every { cpfService.getByCpf(any()) } returns account

        val myDeposit1 = AccountDepositFormDTO("768.849.153-31", 3000.0)
        val myDeposit2 = AccountDepositFormDTO("768.849.153-31", -100.0)

        assertThrows(InvalidTransferException::class.java) { accountService.deposit(myDeposit1) }
        assertThrows(InvalidTransferException::class.java) { accountService.deposit(myDeposit2) }
    }

    @Test
    fun `Given I want to deposit on an account, When I send and invalid account's CPF, Then the deposit must not be made` () {
        val myDeposit = AccountDepositFormDTO("10101010101", 100.0)

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(any()) } returns null
        every { cpfService.clearAndValidateCpf(any()) } returns myDeposit.cpf
        every { cpfService.getByCpf(myDeposit.cpf) } throws AccountNotFoundException()


        assertThrows(AccountNotFoundException::class.java) { accountService.deposit(myDeposit) }
    }

    @Test
    fun `Given I want to transfer to an account, When I have the value on my balance and send the receiver's CPF, Then the transfer is successful` () {
        val senderAccount = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "121314",
            balance = 100.0
        )

        val receiverAccount = AccountModel(
            id = null,
            name = "Zé",
            cpf = "83213884774",
            password = "102030",
            balance = 10.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(senderAccount.cpf) } returns senderAccount
        every { accountRepository.findByCpf(receiverAccount.cpf) } returns receiverAccount
        every { cpfService.clearAndValidateCpf(senderAccount.cpf) } returns senderAccount.cpf
        every { cpfService.clearAndValidateCpf(receiverAccount.cpf) } returns receiverAccount.cpf
        every { cpfService.getByCpf(senderAccount.cpf) } returns senderAccount
        every { cpfService.getByCpf(receiverAccount.cpf) } returns receiverAccount

        val myTransfer = AccountTransferFormDTO(senderAccount.cpf, receiverAccount.cpf, 70.0, "121314")
        val transferResponseDTO = accountService.transfer(myTransfer)

        assertThat(transferResponseDTO).isNotNull
        assertThat(transferResponseDTO.receiverName).isEqualTo(receiverAccount.name)
        assertThat(transferResponseDTO.receivedValue).isEqualTo(70.0)
        assertThat(senderAccount.balance).isEqualTo(30.0)
        assertThat(receiverAccount.balance).isEqualTo(80.0)
    }

    @Test
    fun `Given I want to transfer to an account, When I send an incorrect password, Then the transfer must fail` () {
        val senderAccount = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "121314",
            balance = 100.0
        )

        val receiverAccount = AccountModel(
            id = null,
            name = "Zé",
            cpf = "83213884774",
            password = "102030",
            balance = 10.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(senderAccount.cpf) } returns senderAccount
        every { accountRepository.findByCpf(receiverAccount.cpf) } returns receiverAccount
        every { cpfService.clearAndValidateCpf(senderAccount.cpf) } returns senderAccount.cpf
        every { cpfService.clearAndValidateCpf(receiverAccount.cpf) } returns receiverAccount.cpf
        every { cpfService.getByCpf(senderAccount.cpf) } returns senderAccount
        every { cpfService.getByCpf(receiverAccount.cpf) } returns receiverAccount

        val myTransfer = AccountTransferFormDTO(senderAccount.cpf, receiverAccount.cpf, 70.0, "121212")

        assertThrows(IncorrectPasswordException::class.java) { accountService.transfer(myTransfer) }
    }

    @Test
    fun `Given I want to transfer to an account, When I try to send an value higher than my balance, Then the transfer must fail` () {
        val senderAccount = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "121314",
            balance = 100.0
        )

        val receiverAccount = AccountModel(
            id = null,
            name = "Zé",
            cpf = "83213884774",
            password = "102030",
            balance = 10.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(senderAccount.cpf) } returns senderAccount
        every { accountRepository.findByCpf(receiverAccount.cpf) } returns receiverAccount
        every { cpfService.clearAndValidateCpf(senderAccount.cpf) } returns senderAccount.cpf
        every { cpfService.clearAndValidateCpf(receiverAccount.cpf) } returns receiverAccount.cpf
        every { cpfService.getByCpf(senderAccount.cpf) } returns senderAccount
        every { cpfService.getByCpf(receiverAccount.cpf) } returns receiverAccount

        val myTransfer = AccountTransferFormDTO(senderAccount.cpf, receiverAccount.cpf, 200.0, "121314")

        assertThrows(InsufficientTransferException::class.java) { accountService.transfer(myTransfer) }
    }

    @Test
    fun `Given I want to transfer to an account, When I try to send an negative value, Then the transfer must fail` () {
        val senderAccount = AccountModel(
            id = null,
            name = "Paulo",
            cpf = "76884915331",
            password = "121314",
            balance = 100.0
        )

        val receiverAccount = AccountModel(
            id = null,
            name = "Zé",
            cpf = "83213884774",
            password = "102030",
            balance = 10.0
        )

        every { accountRepository.save(any<AccountModel>()) } answers { firstArg() }
        every { accountRepository.findByCpf(senderAccount.cpf) } returns senderAccount
        every { accountRepository.findByCpf(receiverAccount.cpf) } returns receiverAccount
        every { cpfService.clearAndValidateCpf(senderAccount.cpf) } returns senderAccount.cpf
        every { cpfService.clearAndValidateCpf(receiverAccount.cpf) } returns receiverAccount.cpf
        every { cpfService.getByCpf(senderAccount.cpf) } returns senderAccount
        every { cpfService.getByCpf(receiverAccount.cpf) } returns receiverAccount

        val invalidTransfer2 = AccountTransferFormDTO(senderAccount.cpf, receiverAccount.cpf, -100.0, "121314")

        assertThrows(InvalidTransferException::class.java) { accountService.transfer(invalidTransfer2) }
    }
}
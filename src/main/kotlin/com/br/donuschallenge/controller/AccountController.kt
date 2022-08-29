package com.br.donuschallenge.controller

import com.br.donuschallenge.model.AccountModel
import com.br.donuschallenge.model.dto.AccountCreateFormDTO
import com.br.donuschallenge.model.dto.AccountDepositFormDTO
import com.br.donuschallenge.model.dto.AccountTransferFormDTO
import com.br.donuschallenge.model.dto.AccountTransactionResponseDTO
import com.br.donuschallenge.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping("/account/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    fun getAccountById(@PathVariable id: String): AccountModel {
        return accountService.getAccountById(id)
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @Valid @RequestBody formDTO: AccountCreateFormDTO,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<AccountCreateFormDTO> {
        val accountDTO = accountService.createAccount(formDTO)
        val uri = uriBuilder.path("/account/${accountDTO.id}").build().toUri()
        return ResponseEntity.created(uri).body(formDTO)
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    fun deposit(
        @Valid @RequestBody formDTO: AccountDepositFormDTO,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<AccountTransactionResponseDTO> {
        return ResponseEntity.ok(accountService.deposit(formDTO))
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    fun transfer(
        @Valid @RequestBody formDTO: AccountTransferFormDTO
    ): ResponseEntity<AccountTransactionResponseDTO> {
       return ResponseEntity.ok(accountService.transfer(formDTO))
    }




}
package com.br.donuschallenge.service

import com.br.donuschallenge.exception.InvalidPasswordException
import org.springframework.stereotype.Service

@Service
class PasswordService {

    fun generateRandomPassword(): String = List(6) { ('0'..'9').random() }.joinToString("")

    fun validatePasswordLength(password: String) {
        if (password.length != 6) throw InvalidPasswordException()
    }

}
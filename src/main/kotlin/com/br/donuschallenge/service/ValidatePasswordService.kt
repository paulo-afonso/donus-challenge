package com.br.donuschallenge.service

import com.br.donuschallenge.commons.Constants
import com.br.donuschallenge.exception.InvalidPasswordException
import org.springframework.stereotype.Service

@Service
class ValidatePasswordService {
    val size = Constants.PASSWORD_SIZE

    fun generateRandomPassword(): String = List(size) { ('0'..'9').random() }.joinToString("")

    fun validatePasswordLength(password: String) {
        if (password.length != size) throw InvalidPasswordException()
    }
}
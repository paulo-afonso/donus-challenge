package com.br.donuschallenge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication
class DonusChallengeApplication

fun main(args: Array<String>) {
	runApplication<DonusChallengeApplication>(*args)
}

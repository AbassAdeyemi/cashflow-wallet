package com.hayba.walletapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableScheduling
@SpringBootApplication
class WalletAppApplication

fun main(args: Array<String>) {
    runApplication<WalletAppApplication>(*args)
}

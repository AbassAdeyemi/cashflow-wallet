package walletbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WalletBackendApplication

fun main(args: Array<String>) {
    runApplication<WalletBackendApplication>(*args)
}

package br.com.zup.edu.pix.bancocentral

import br.com.zup.edu.pix.bancocentral.registra.AccountType
import io.micronaut.core.annotation.Introspected

@Introspected
data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {

}

package br.com.zup.edu.pix.bancocentral.registra

import br.com.zup.edu.pix.bancocentral.BankAccount
import br.com.zup.edu.pix.bancocentral.Owner
import br.com.zup.edu.pix.bancocentral.PixKeyType
import io.micronaut.core.annotation.Introspected

@Introspected
data class CreatePixKeyRequest(
    val keyType: PixKeyType,
    val key: String?,
    val bankAccount: BankAccount?,
    val owner: Owner?
) {

}
package br.com.zup.edu.pix.mostra

import br.com.zup.edu.pix.ContaAssociada
import br.com.zup.edu.pix.TipoConta
import br.com.zup.edu.pix.bancocentral.BankAccount
import br.com.zup.edu.pix.bancocentral.Owner
import br.com.zup.edu.pix.bancocentral.PixKeyType
import br.com.zup.edu.pix.bancocentral.registra.AccountType
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class PixKeyDetailsResponse(
    val keyType: PixKeyType,
    val key: String?,
    val bankAccount: BankAccount?,
    val owner: Owner?,
    val createdAt: LocalDateTime
) {
    fun toModel(): MostraPixResponse {
        return MostraPixResponse(
            tipo = keyType.tipoChave,
            chave = key,
            tipoConta = when(this.bankAccount?.accountType){
                AccountType.CACC -> TipoConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoConta.CONTA_POUPANCA
                null -> TODO()
            },
            contaAssociada = ContaAssociada(
                instituicao = bankAccount.participant,
                nomeDoTitular = owner!!.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }

}

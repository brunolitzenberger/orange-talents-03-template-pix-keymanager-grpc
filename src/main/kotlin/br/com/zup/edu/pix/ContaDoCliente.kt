package br.com.zup.edu.pix

import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaDoCliente(
     val tipo: TipoConta,
     val instituicao: Instituicao,
     val agencia: String,
     val numero: String,
     val titular: Titular
) {

    fun toModel(): ContaAssociada{
        return ContaAssociada(
            instituicao = instituicao.nome,
            nomeDoTitular = titular.nome,
            cpfDoTitular = titular.cpf,
            agencia = agencia,
            numeroDaConta = numero
        )
    }

}

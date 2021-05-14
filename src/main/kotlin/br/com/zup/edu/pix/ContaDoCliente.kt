package br.com.zup.edu.pix

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaDoCliente(
    @JsonProperty val tipo: TipoConta,
    @JsonProperty val instituicao: Instituicao,
    @JsonProperty val agencia: String,
    @JsonProperty val numero: String,
    @JsonProperty val titular: Titular
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

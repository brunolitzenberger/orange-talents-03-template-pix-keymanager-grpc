package br.com.zup.edu.pix.registra

import br.com.zup.edu.pix.ContaAssociada
import br.com.zup.edu.pix.Pix
import br.com.zup.edu.pix.TipoChave
import br.com.zup.edu.pix.TipoConta
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ChavePixDTO(
    @field:NotBlank
    @JsonProperty val clienteId: String?,
    @field:NotNull
    @JsonProperty val tipoChave: TipoChave?,

    @field:NotBlank
    @JsonProperty val chave: String?,

    @field:NotNull
    @JsonProperty val tipoConta: TipoConta?

) {

    init {
        if(tipoChave?.getValidacao()?.valida(chave) == false){
            throw ChaveInvalidaException("Chave incompatível com o tipo de chave: ${tipoChave}")
        }
    }

    fun toPix(conta: ContaAssociada?): Pix {
        return Pix(
            clienteId = UUID.fromString(clienteId),
            tipoChave = tipoChave,
            chave = chave,
            tipoConta = tipoConta,
            conta = conta
        )
    }

}
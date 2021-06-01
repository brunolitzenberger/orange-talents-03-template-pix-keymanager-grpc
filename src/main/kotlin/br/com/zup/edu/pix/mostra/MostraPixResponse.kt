package br.com.zup.edu.pix.mostra

import br.com.zup.edu.pix.ContaAssociada
import br.com.zup.edu.pix.Pix
import br.com.zup.edu.pix.TipoChave
import br.com.zup.edu.pix.TipoConta
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import java.util.*

@Introspected
data class MostraPixResponse(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipo: TipoChave?,
    val chave: String?,
    val tipoConta: TipoConta?,
    val contaAssociada: ContaAssociada?,
    val instanteRegistro: LocalDateTime = LocalDateTime.now()
) {
    companion object{
        fun new(pix: Pix): MostraPixResponse{
            return MostraPixResponse(
                pixId = pix.id,
                clienteId = pix.clienteId,
                tipo = pix.tipoChave,
                chave = pix.chave,
                tipoConta = pix.tipoConta,
                contaAssociada = pix.conta,
                instanteRegistro = pix.instante
            )
        }
    }



}
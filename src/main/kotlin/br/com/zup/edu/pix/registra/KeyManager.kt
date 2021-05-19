package br.com.zup.edu.pix.grpc

import br.com.zup.edu.KeyManagerRequest
import br.com.zup.edu.TipoChave.CHAVE_DESCONHECIDA
import br.com.zup.edu.TipoConta.CONTA_DESCONHECIDA
import br.com.zup.edu.pix.TipoChave
import br.com.zup.edu.pix.TipoConta
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import br.com.zup.edu.pix.exceptions.ContaInvalidaException
import br.com.zup.edu.pix.registra.ChavePixDTO

fun KeyManagerRequest.toDTO(): ChavePixDTO {
    return ChavePixDTO(
        clienteId = clienteId,
        tipoChave = when (tipoChave) {
            CHAVE_DESCONHECIDA -> throw ChaveInvalidaException("Tipo de chave desconhecido.")
            else -> TipoChave.valueOf(tipoChave.name)
        },
        chave = chave,
        tipoConta = when (tipoConta) {
            CONTA_DESCONHECIDA -> throw ContaInvalidaException("Tipo de conta desconhecido.")
            else -> TipoConta.valueOf(tipoConta.name)
        }
    )


}
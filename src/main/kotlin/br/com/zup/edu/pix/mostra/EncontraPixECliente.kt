package br.com.zup.edu.pix.mostra

import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import io.micronaut.core.annotation.Introspected
import java.lang.RuntimeException
import java.util.*
import javax.validation.constraints.NotBlank

@Introspected
data class EncontraPixECliente(@field:NotBlank val clienteId: String, @field:NotBlank val pixId: String) :
    FiltroDeChaves {

    override fun filtra(pixRepository: PixRepository, bancoCentralClient: BancoCentralClient): MostraPixResponse {
        return pixRepository
            .findById(UUID.fromString(pixId))
            .filter { it.validaDono(UUID.fromString(clienteId)) }
            .map { MostraPixResponse.new(it) }
            .orElseThrow { ChaveNaoEncontradaException("Chave não encontrada") }
    }


}
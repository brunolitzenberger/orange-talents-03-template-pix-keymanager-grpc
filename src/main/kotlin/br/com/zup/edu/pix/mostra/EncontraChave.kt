package br.com.zup.edu.pix.mostra

import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import io.micronaut.core.annotation.Introspected
import java.lang.RuntimeException
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Introspected
data class EncontraChave(@field:NotBlank val chave: String): FiltroDeChaves {

    override fun filtra(pixRepository: PixRepository, bancoCentralClient: BancoCentralClient): MostraPixResponse {
        return pixRepository.findByChave(chave)
            .map { MostraPixResponse.new(it) }
            .orElseGet{
                val chave = bancoCentralClient.encontraChave(chave) ?: throw ChaveNaoEncontradaException("Chave não encontrada")
                chave.toModel()
            }
    }
}
package br.com.zup.edu.pix.mostra

import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient

interface FiltroDeChaves {

    fun filtra(pixRepository: PixRepository, bancoCentralClient: BancoCentralClient): MostraPixResponse
}
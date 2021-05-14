package br.com.zup.edu.pix.remove

import br.com.zup.edu.pix.Instituicao
import io.micronaut.core.annotation.Introspected

@Introspected
data class DadosCliente(
    var id: String,
    var nome: String,
    var cpf: String,
    var instituicao: Instituicao
) {
}
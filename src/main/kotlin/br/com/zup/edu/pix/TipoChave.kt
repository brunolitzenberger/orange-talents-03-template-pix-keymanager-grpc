package br.com.zup.edu.pix

import br.com.zup.edu.pix.registra.validation.*

enum class TipoChave(val valida: ValidaTipoChave) {
    CPF(ValidaCpf()),
    TELEFONE(ValidaTelefone()),
    EMAIL(ValidaEmail()),
    ALEATORIA(ValidaAleatoria());


    fun getValidacao(): ValidaTipoChave {
        return valida
    }
}
package br.com.zup.edu.pix.registra.validation

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

class ValidaCpf : ValidaTipoChave {

    override fun valida(chave: String?): Boolean {
        if (chave.isNullOrBlank()) {
            return false
        }

        if (!chave.matches("^[0-9]{11}\$".toRegex())) {
            return false
        }

        return CPFValidator().run {
            initialize(null)
            isValid(chave, null)
        }

    }
}
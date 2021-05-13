package br.com.zup.edu.pix.registra.validation

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator


    class ValidaEmail : ValidaTipoChave {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }

        }

    }

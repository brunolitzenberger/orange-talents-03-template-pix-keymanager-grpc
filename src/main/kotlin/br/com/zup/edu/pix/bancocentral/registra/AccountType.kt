package br.com.zup.edu.pix.bancocentral.registra

import br.com.zup.edu.pix.TipoConta
import br.com.zup.edu.pix.TipoConta.CONTA_CORRENTE


enum class AccountType {
    CACC, SVGS;

    companion object {
        fun by(tipoConta: TipoConta?): AccountType {
            return when (tipoConta) {
                CONTA_CORRENTE -> CACC
                else ->  SVGS
            }
        }
    }

}

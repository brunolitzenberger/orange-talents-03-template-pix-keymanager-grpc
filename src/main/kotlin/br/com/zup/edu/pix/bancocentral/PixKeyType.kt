package br.com.zup.edu.pix.bancocentral

import br.com.zup.edu.pix.TipoChave

enum class PixKeyType(val tipoChave: TipoChave?) {
    CPF(TipoChave.CPF),
    PHONE(TipoChave.TELEFONE),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIA);



    companion object {

        private val mapping = PixKeyType.values().associateBy(PixKeyType::tipoChave)

        fun by(tipoChave: TipoChave?): PixKeyType {
            return  mapping[tipoChave] ?: throw IllegalArgumentException("PixKeyType invalid or not found for $tipoChave")
        }
    }
}

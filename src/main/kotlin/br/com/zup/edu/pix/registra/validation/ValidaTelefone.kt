package br.com.zup.edu.pix.registra.validation

class ValidaTelefone : ValidaTipoChave {
    override fun valida(chave: String?): Boolean {
        if (chave.isNullOrBlank()) {
            return false
        }
        return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())

    }

}

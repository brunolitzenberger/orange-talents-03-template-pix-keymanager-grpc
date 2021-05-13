package br.com.zup.edu.pix.registra.validation

class ValidaAleatoria : ValidaTipoChave {
    override fun valida(chave: String?): Boolean {
        if (!chave.isNullOrBlank()) {
            return false
        }
        return true
    }

}

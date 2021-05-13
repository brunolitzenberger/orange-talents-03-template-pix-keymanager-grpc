package br.com.zup.edu.pix

import com.fasterxml.jackson.annotation.JsonProperty

data class Instituicao(
    @JsonProperty val nome: String,
    @JsonProperty val ispb: String
) {

}

package br.com.zup.edu.pix

import com.fasterxml.jackson.annotation.JsonProperty

data class Titular(
    @JsonProperty val id: String,
    @JsonProperty val nome: String,
    @JsonProperty val cpf: String
) {

}

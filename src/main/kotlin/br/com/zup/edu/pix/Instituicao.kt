package br.com.zup.edu.pix

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Instituicao(
   val nome: String,
   val ispb: String
) {

}

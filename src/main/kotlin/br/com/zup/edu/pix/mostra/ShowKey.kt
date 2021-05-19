@file:Suppress("UNREACHABLE_CODE")

package br.com.zup.edu.pix.mostra

import br.com.zup.edu.ShowPixKeyRequest
import br.com.zup.edu.ShowPixKeyRequest.FilterCase.*
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

fun ShowPixKeyRequest.toModel(validator: Validator): FiltroDeChaves {

     val filtrados =  when (filterCase!!) {
        PIXID ->  pixId.let {  EncontraPixECliente( it.clienteId, it.pixId) }
        CHAVEPIX -> EncontraChave(chavePix)
        FILTER_NOT_SET -> TODO()
    }

    val validacoes = validator.validate(filtrados)
    if(validacoes.isNotEmpty()){
        throw ConstraintViolationException(validacoes)
    }
    return filtrados
}
package br.com.zup.edu.pix.bancocentral

import br.com.zup.edu.pix.bancocentral.registra.OwnerType
import io.micronaut.core.annotation.Introspected

@Introspected
data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
){



}

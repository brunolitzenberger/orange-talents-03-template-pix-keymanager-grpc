package br.com.zup.edu.pix.bancocentral

import br.com.zup.edu.pix.bancocentral.delete.DeletePixKeyRequest
import br.com.zup.edu.pix.bancocentral.delete.DeletePixKeyResponse
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyRequest
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyResponse
import br.com.zup.edu.pix.mostra.PixKeyDetailsResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8082")
interface BancoCentralClient {

    @Post(
        "/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun create(@Body request: CreatePixKeyRequest): CreatePixKeyResponse?

    @Delete(
        "/api/v1/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun delete(@Body request: DeletePixKeyRequest) : DeletePixKeyResponse


    @Get("/api/v1/pix/keys/{key}")
    fun encontraChave(@PathVariable key: String) : PixKeyDetailsResponse?

}
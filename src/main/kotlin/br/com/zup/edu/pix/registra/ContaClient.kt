package br.com.zup.edu.pix.registra

import br.com.zup.edu.pix.ContaDoCliente
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:9091/api/v1/clientes")
interface ContaClient {

    @Get("/{clienteId}/contas")
    fun buscaConta(@PathVariable clienteId: String?, @QueryValue tipo: String?): ContaDoCliente?

}
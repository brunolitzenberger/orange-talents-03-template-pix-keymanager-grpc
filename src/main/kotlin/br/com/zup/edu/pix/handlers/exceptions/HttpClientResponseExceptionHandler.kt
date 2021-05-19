package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveExistenteException
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Singleton
class HttpClientResponseExceptionHandler : ExceptionHandler<HttpClientResponseException> {

    override fun handle(e: HttpClientResponseException): StatusWithDetails {
        return StatusWithDetails(
            Status.INTERNAL
                .withDescription("Falha na comunicação com serviço externo.")
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is HttpClientResponseException
    }
}
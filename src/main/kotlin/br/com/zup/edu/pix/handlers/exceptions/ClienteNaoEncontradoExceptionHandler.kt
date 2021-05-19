package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ClienteNaoEncontradoExceptionHandler : ExceptionHandler<ClienteNaoEncontradoException> {

    override fun handle(e: ClienteNaoEncontradoException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ClienteNaoEncontradoException
    }
}
package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveNaoEncontradaExceptionHandler : ExceptionHandler<ChaveNaoEncontradaException> {

    override fun handle(e: ChaveNaoEncontradaException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveNaoEncontradaException
    }
}
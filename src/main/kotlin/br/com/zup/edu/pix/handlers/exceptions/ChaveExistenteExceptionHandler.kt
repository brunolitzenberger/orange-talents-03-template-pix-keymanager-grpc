package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveExistenteException
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveExistenteExceptionHandler : ExceptionHandler<ChaveExistenteException> {

    override fun handle(e: ChaveExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveExistenteException
    }
}
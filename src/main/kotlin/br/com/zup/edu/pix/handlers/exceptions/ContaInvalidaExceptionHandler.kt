package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveExistenteException
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.exceptions.ContaInvalidaException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ContaInvalidaExceptionHandler : ExceptionHandler<ContaInvalidaException> {

    override fun handle(e: ContaInvalidaException): StatusWithDetails {
        return StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ContaInvalidaException
    }
}
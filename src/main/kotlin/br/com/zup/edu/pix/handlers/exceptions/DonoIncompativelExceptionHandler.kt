package br.com.zup.edu.pix.handlers.exceptions

import br.com.zup.edu.pix.exceptions.ChaveExistenteException
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.exceptions.DonoIncompativelException
import br.com.zup.edu.pix.handlers.ExceptionHandler
import br.com.zup.edu.pix.handlers.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class DonoIncompativelExceptionHandler : ExceptionHandler<DonoIncompativelException> {

    override fun handle(e: DonoIncompativelException): StatusWithDetails {
        return StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is DonoIncompativelException
    }
}
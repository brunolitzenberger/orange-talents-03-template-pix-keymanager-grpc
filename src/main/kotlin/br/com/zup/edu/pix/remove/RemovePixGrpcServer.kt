package br.com.zup.edu.pix.remove

import br.com.zup.edu.DeletePixKeyRequest
import br.com.zup.edu.DeletePixKeyResponse
import br.com.zup.edu.KeyManagerRemoveServiceGrpc
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.exceptions.ChaveNaoEncontradaException
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException
import br.com.zup.edu.pix.exceptions.DonoIncompativelException
import br.com.zup.edu.pix.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemovePixGrpcServer(
    @Inject val repository: PixRepository,
    @Inject val clienteClient: ClienteClient,
    @Inject val bancoCentralClient: BancoCentralClient
) : KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceImplBase() {

    override fun remover(request: DeletePixKeyRequest, responseObserver: StreamObserver<DeletePixKeyResponse>) {
        val pix = repository.findById(UUID.fromString(request.pixId))
        if (!pix.isPresent) {
            throw ChaveNaoEncontradaException("Chave não encontrada")
        }

        val dadosDoCliente = clienteClient.buscaDados(request.clientId) ?: throw ClienteNaoEncontradoException("Cliente não encontrado.")

        if (!pix.get().validaDono(UUID.fromString(dadosDoCliente.id))) {
            throw DonoIncompativelException("Dono da chave inválido.")
        }

        bancoCentralClient.delete(pix.get().toDeletePixRequest())

        repository.deleteById(pix.get().id!!)
        responseObserver.onNext(DeletePixKeyResponse.newBuilder().setMessage("Chave removida.").build())
        responseObserver.onCompleted()
    }

}
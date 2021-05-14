package br.com.zup.edu.pix.remove

import br.com.zup.edu.DeletePixKeyRequest
import br.com.zup.edu.DeletePixKeyResponse
import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRemoveServiceGrpc
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemovePixGrpcServer(@Inject val repository: PixRepository, @Inject val clienteClient: ClienteClient) :
    KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceImplBase() {

    override fun remover(request: DeletePixKeyRequest, responseObserver: StreamObserver<DeletePixKeyResponse>) {
        val pix = repository.findById(UUID.fromString(request.pixId))
        if (!pix.isPresent) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Chave não encontrada")
                    .asRuntimeException()
            )
            return
        }

        val dadosDoCliente = clienteClient.buscaDados(request.clientId)
        if (dadosDoCliente == null) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Cliente não encontrado.")
                    .asRuntimeException()
            )
            return
        }

        if (!pix.get().conta?.validaDono(dadosDoCliente.cpf)!!) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Dono da chave não compatível.")
                    .asRuntimeException()
            )
            return
        }
        repository.deleteById(pix.get().id!!)
        responseObserver.onNext(DeletePixKeyResponse.newBuilder().setMessage("Chave removida.").build())
        responseObserver.onCompleted()
    }

}
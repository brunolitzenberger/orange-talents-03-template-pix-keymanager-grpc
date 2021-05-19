package br.com.zup.edu.pix.lista

import br.com.zup.edu.*
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException
import br.com.zup.edu.pix.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ListaTodosServer(@Inject val pixRepository: PixRepository) :
    KeyManagerShowAllServiceGrpc.KeyManagerShowAllServiceImplBase() {

    override fun all(request: ShowAllPixKeyRequest, responseObserver: StreamObserver<ShowAllPixKeyResponse>) {
        if (request.clienteId.isNullOrBlank()) {
            throw ClienteNaoEncontradoException("Cliente não encontrado.")
        }
        val todos = pixRepository.findAllByClienteId(UUID.fromString(request.clienteId)).map {
            ShowAllPixKeyResponse.ChavePix
                .newBuilder()
                .setPixId(it.id.toString())
                .setChave(it.chave)
                .setTipoChave(TipoChave.valueOf(it.tipoChave!!.name))
                .setTipoConta(TipoConta.valueOf(it.tipoConta!!.name))
                .setCreatedAt(it.instante.toString())
                .build()
        }

        responseObserver.onNext(ShowAllPixKeyResponse
            .newBuilder()
            .addAllChavePix(todos)
            .setClienteId(request.clienteId)
            .build())
        responseObserver.onCompleted()


    }
}
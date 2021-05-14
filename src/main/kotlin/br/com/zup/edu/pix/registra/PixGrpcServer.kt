package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRequest
import br.com.zup.edu.KeyManagerResponse
import br.com.zup.edu.pix.ContaAssociada
import br.com.zup.edu.pix.ContaDoCliente
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import br.com.zup.edu.pix.grpc.toDTO
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixGrpcServer(@Inject val repository: PixRepository, @Inject val contaClient: ContaClient): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun registrar(request: KeyManagerRequest, responseObserver: StreamObserver<KeyManagerResponse>) {
        if (repository.existsByChave(request.chave)) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription("Chave já registrada")
                    .asRuntimeException()
            )
            return
        }
        try {
            val pixDTO = request.toDTO()
            val conta: ContaDoCliente = contaClient.buscaConta(
                pixDTO.clienteId,
                pixDTO.tipoConta.toString()
            ) ?: throw ContaNaoExisteException("Conta não existe")

            val contaAssociada: ContaAssociada = conta.toModel()
            val pix = repository.save(pixDTO.toPix(contaAssociada))
            responseObserver.onNext(KeyManagerResponse.newBuilder().setPixId(pix.id.toString()).build())
            responseObserver.onCompleted()
        } catch (e: ChaveInvalidaException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }catch (e: ContaNaoExisteException){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }
    }

}
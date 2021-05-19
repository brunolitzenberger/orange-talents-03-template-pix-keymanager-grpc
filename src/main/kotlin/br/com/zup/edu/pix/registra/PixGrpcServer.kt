package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRequest
import br.com.zup.edu.KeyManagerResponse
import br.com.zup.edu.pix.ContaAssociada
import br.com.zup.edu.pix.ContaDoCliente
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyResponse
import br.com.zup.edu.pix.exceptions.ChaveExistenteException
import br.com.zup.edu.pix.exceptions.ChaveInvalidaException
import br.com.zup.edu.pix.exceptions.ContaNaoEncontradaException
import br.com.zup.edu.pix.grpc.toDTO
import br.com.zup.edu.pix.handlers.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorHandler
class PixGrpcServer(
    @Inject val repository: PixRepository,
    @Inject val contaClient: ContaClient,
    @Inject val bcb: BancoCentralClient
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun registrar(request: KeyManagerRequest, responseObserver: StreamObserver<KeyManagerResponse>) {
        if (repository.existsByChave(request.chave)) {
            throw ChaveExistenteException("Chave já registrada")
        }
            val pixDTO = request.toDTO()

            val conta: ContaDoCliente = contaClient.buscaConta(
                pixDTO.clienteId,
                pixDTO.tipoConta.toString()
            ) ?: throw ContaNaoEncontradaException("Conta não existe")

            val contaAssociada: ContaAssociada = conta.toModel()
            val pix = repository.save(pixDTO.toPix(contaAssociada))
            val bcbRequest =  pix.toCreatePixRequest()

            val bancoCentralResponse: CreatePixKeyResponse? = bcb.create(bcbRequest)

            pix.atualizaChave(bancoCentralResponse?.key)
            repository.update(pix)

            responseObserver.onNext(KeyManagerResponse.newBuilder().setPixId(pix.id.toString()).build())
            responseObserver.onCompleted()


    }

}
package br.com.zup.edu.pix.mostra

import br.com.zup.edu.*
import br.com.zup.edu.pix.PixRepository
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorHandler
class MostraPixGrpcServer(
    @Inject val repository: PixRepository,
    @Inject val bancoCentralClient: BancoCentralClient,
    @Inject val validator: Validator
) : KeyManagerShowServiceGrpc.KeyManagerShowServiceImplBase() {

    override fun listar(
        request: ShowPixKeyRequest,
        responseObserver: StreamObserver<ShowPixKeyResponse>
    ) {
        val filtroDeChaves = request.toModel(validator)
        val response = filtroDeChaves.filtra(repository, bancoCentralClient)
        responseObserver.onNext(
            ShowPixKeyResponse
                .newBuilder()
                .setPixId(response.pixId.toString() ?: "")
                .setClienteId(response.clienteId.toString() ?: "")
                .setChave(
                    ShowPixKeyResponse.ChavePix.newBuilder()
                        .setTipo(TipoChave.valueOf(response.tipo!!.name))
                        .setChave(response.chave)
                        .setConta(
                            ShowPixKeyResponse.ChavePix.MostrarPixResponse
                                .newBuilder()
                                .setTipo(TipoConta.valueOf(response.tipoConta!!.name))
                                .setAgencia(response.contaAssociada?.agencia)
                                .setCpf(response.contaAssociada?.cpfDoTitular)
                                .setInstituicao(response.contaAssociada?.instituicao)
                                .setNumeroDaConta(response.contaAssociada?.numeroDaConta)
                                .build()
                        )
                        .setCriadaEm(response.instanteRegistro.toString())
                        .build()
                )
                .build()
        )
        responseObserver.onCompleted()
    }


}

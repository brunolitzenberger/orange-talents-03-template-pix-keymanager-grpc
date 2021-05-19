package br.com.zup.edu.pix.remove

import br.com.zup.edu.DeletePixKeyRequest
import br.com.zup.edu.KeyManagerRemoveServiceGrpc
import br.com.zup.edu.pix.*
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.bancocentral.delete.DeletePixKeyResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import br.com.zup.edu.pix.bancocentral.delete.DeletePixKeyRequest as DeleteKeyRequest

@MicronautTest(transactional = false)
internal class RemovePixGrpcServerTest(
    @Inject val repository: PixRepository,
    @Inject val grpcCLient: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub,
    @Inject val bancoCentralClient: BancoCentralClient
) {

    val instituicao: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
    val titular: Titular = Titular("c56dfef4-7901-44fb-84e2-a2cefb157890", "Rafael M C Ponte", "02467781054")
    val contaDoCliente: ContaDoCliente = ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao, "0001", "291900", titular)
    val pix : Pix = Pix(UUID.fromString(titular.id), TipoChave.CPF, titular.cpf, contaDoCliente.tipo, contaDoCliente.toModel())

    @BeforeEach
    fun setup(){
        repository.save(pix)
    }

    @Test
    fun `deve remover uma chave pix`(){
        val requestBcb: DeleteKeyRequest = pix.toDeletePixRequest()
        val responseBcb: DeletePixKeyResponse = DeletePixKeyResponse(pix.chave, "123", LocalDateTime.now())
        Mockito.`when`(bancoCentralClient.delete(
            requestBcb
        )).thenReturn(responseBcb)

        val response = grpcCLient.remover(
            DeletePixKeyRequest
                .newBuilder()
                .setClientId(titular.id)
                .setPixId(pix.id.toString())
                .build()
        )
        val id = repository.findById(pix.id!!)
        assertTrue(!id.isPresent)
        assertEquals(response.message, "Chave removida.")
    }



    @Test
    fun `deve gerar erro quando nao encontrar a chave pix`(){
        val error = assertThrows<StatusRuntimeException>{
            grpcCLient.remover(
                DeletePixKeyRequest
                    .newBuilder()
                    .setClientId(titular.id)
                    .setPixId(UUID.randomUUID().toString())
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada", status.description)
        }
    }

    @Test
    fun `deve gerar erro quando nao encontrar cliente`(){
        val error = assertThrows<StatusRuntimeException>{
            grpcCLient.remover(
                DeletePixKeyRequest
                    .newBuilder()
                    .setClientId(UUID.randomUUID().toString())
                    .setPixId(pix.id.toString())
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado.", status.description)
        }
    }

    @Test
    fun `deve gerar erro quando cliente nao for dono da chave`(){
        val error = assertThrows<StatusRuntimeException>{
            grpcCLient.remover(
                DeletePixKeyRequest
                    .newBuilder()
                    .setClientId("5260263c-a3c1-4727-ae32-3bdb2538841b")
                    .setPixId(pix.id.toString())
                    .build()
            )
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dono da chave inválido.", status.description)
        }
    }

    @AfterEach
    fun remove(){
        repository.deleteAll()
    }


    @MockBean(BancoCentralClient::class)
    fun bancoMock(): BancoCentralClient {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    @Factory
    class GrpcRemoveFactory {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub {
            return KeyManagerRemoveServiceGrpc.newBlockingStub(channel)
        }
    }
}
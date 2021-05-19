package br.com.zup.edu.pix.mostra

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerShowServiceGrpc
import br.com.zup.edu.ShowPixKeyRequest
import br.com.zup.edu.ShowPixKeyResponse
import br.com.zup.edu.pix.*
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.bancocentral.PixKeyType
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class MostraPixGrpcServerTest(
    @Inject val repository: PixRepository,
    @Inject val grpcClient: KeyManagerShowServiceGrpc.KeyManagerShowServiceBlockingStub,
    @Inject val bancoCentralClient: BancoCentralClient
){

    val instituicao: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
    val titular: Titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Rafael M C Ponte", "02467781054")
    val contaDoCliente: ContaDoCliente = ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao, "0001", "291900", titular)
    val pix : Pix = Pix(UUID.fromString(titular.id), TipoChave.ALEATORIA, "", contaDoCliente.tipo, contaDoCliente.toModel())

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve trazer os dados por chave pix`(){
        val pixSalvo = repository.save(pix)
        val response = grpcClient.listar(ShowPixKeyRequest
            .newBuilder()
            .setChavePix(pixSalvo.chave)
            .build())
        assertNotNull(response)
    }

    @Test
    fun `deve trazer os dados por pixId e clienteId`(){
        val pixSalvo = repository.save(pix)
        val response = grpcClient.listar(ShowPixKeyRequest
            .newBuilder()
            .setPixId(ShowPixKeyRequest.PixIdFilter
                .newBuilder()
                .setClienteId(pixSalvo.clienteId.toString())
                .setPixId(pixSalvo.id.toString()))
            .build())
        assertNotNull(response)
    }

    @Test
    fun `deve trazer os dados por chave do banco central caso nao encontre no banco local`(){
        val pixKeyDetailsResponse = PixKeyDetailsResponse(
            keyType = PixKeyType.EMAIL,
            key = "teste@teste2.com",
            bankAccount = contaDoCliente.toModel().toBankAccount(TipoConta.CONTA_CORRENTE),
            owner = contaDoCliente.toModel().toOwner(),
            createdAt = LocalDateTime.now()
        )
        Mockito.`when`(bancoCentralClient
            .encontraChave("teste@teste2.com"))
            .thenReturn(pixKeyDetailsResponse)
        val response = grpcClient.listar(ShowPixKeyRequest
            .newBuilder()
            .setChavePix("teste@teste2.com")
            .build())
        assertNotNull(response)
    }

    @Test
    fun `deve gerar erro ao nao encontrar uma chave`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.listar(
                ShowPixKeyRequest
                    .newBuilder()
                    .setChavePix("teste2@teste.com")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada", status.description)
        }
    }

    @Test
    fun `deve gerar erro ao nao econtrar por pixId e clienteId`(){
        val pixSalvo = repository.save(pix)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.listar(ShowPixKeyRequest
                .newBuilder()
                .setPixId(ShowPixKeyRequest.PixIdFilter
                    .newBuilder()
                    .setClienteId(UUID.randomUUID().toString())
                    .setPixId(pixSalvo.id.toString()))
                .build())
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada", status.description)
        }
    }



    @MockBean(BancoCentralClient::class)
    fun bancoMock(): BancoCentralClient {
        return Mockito.mock(BancoCentralClient::class.java)
    }


    @Factory
    class GrpcFactory {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerShowServiceGrpc.KeyManagerShowServiceBlockingStub{
            return KeyManagerShowServiceGrpc.newBlockingStub(channel)
        }

    }
}
package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoChave.*
import br.com.zup.edu.TipoConta.CONTA_CORRENTE
import br.com.zup.edu.TipoConta.CONTA_DESCONHECIDA
import br.com.zup.edu.pix.*
import br.com.zup.edu.pix.bancocentral.BancoCentralClient
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyRequest
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyResponse
import br.com.zup.edu.pix.bancocentral.PixKeyType
import br.com.zup.edu.pix.mostra.PixKeyDetailsResponse
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import br.com.zup.edu.pix.TipoChave as TipoChavePix

@MicronautTest(transactional = false)
    internal class PixGrpcServerTest(
    val repository: PixRepository,
    val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
){

    @field:Inject
    lateinit var contaClient: ContaClient

    @field:Inject
    lateinit var bancoCentralClient: BancoCentralClient


    val instituicao: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
    val titular: Titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Rafael M C Ponte", "02467781054")
    val contaDoCliente: ContaDoCliente = ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao, "0001", "291900", titular)



    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }


    @Test
    fun `deve registrar uma chave pix do tipo aleatoria`(){
        val pix : Pix = Pix(UUID.fromString(titular.id), TipoChavePix.ALEATORIA, "", contaDoCliente.tipo, contaDoCliente.toModel())
        val requestBcb: CreatePixKeyRequest = pix.toCreatePixRequest()
        val responseBcb: CreatePixKeyResponse = CreatePixKeyResponse(
            PixKeyType.by(pix.tipoChave), pix.chave,
            pix.conta?.toBankAccount(pix.tipoConta), pix.conta?.toOwner(), LocalDateTime.now()
        )
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        Mockito.`when`(bancoCentralClient
            .create(requestBcb))
            .thenReturn(responseBcb)
        val response = grpcClient.registrar(
            KeyManagerRequest
                .newBuilder()
                .setChave("")
                .setTipoChave(ALEATORIA)
                .setTipoConta(CONTA_CORRENTE)
                .setClienteId(titular.id)
                .build()
        )
        assertNotNull(response.pixId)
        assertTrue(repository.existsById(UUID.fromString(response.pixId)))
    }



    @Test
    fun `deve registrar uma chave pix do tipo cpf`(){
        //Cenário
        val pix : Pix = Pix(UUID.fromString(titular.id), TipoChavePix.CPF, titular.cpf, contaDoCliente.tipo, contaDoCliente.toModel())
        val requestBcb: CreatePixKeyRequest = pix.toCreatePixRequest()
        val responseBcb: CreatePixKeyResponse = CreatePixKeyResponse(
            PixKeyType.by(pix.tipoChave), pix.chave,
            pix.conta?.toBankAccount(pix.tipoConta), pix.conta?.toOwner(), LocalDateTime.now()
        )
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        Mockito.`when`(bancoCentralClient
            .create(requestBcb))
            .thenReturn(responseBcb)
        //Ação
        val response = grpcClient.registrar(
            KeyManagerRequest
                .newBuilder()
                .setChave(titular.cpf)
                .setTipoChave(CPF)
                .setTipoConta(CONTA_CORRENTE)
                .setClienteId(titular.id)
                .build()
        )
        //Validação
        assertNotNull(response.pixId)
        assertTrue(repository.existsById(UUID.fromString(response.pixId)))
    }

    @Test
    fun `deve registrar uma chave pix do tipo email`(){
        //Cenário
        val pix : Pix = Pix(UUID.fromString(titular.id), TipoChavePix.EMAIL, "teste@teste.com", contaDoCliente.tipo, contaDoCliente.toModel())
        val requestBcb: CreatePixKeyRequest = pix.toCreatePixRequest()
        val responseBcb: CreatePixKeyResponse = CreatePixKeyResponse(
            PixKeyType.by(pix.tipoChave), pix.chave,
            pix.conta?.toBankAccount(pix.tipoConta), pix.conta?.toOwner(), LocalDateTime.now()
        )
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        Mockito.`when`(bancoCentralClient
            .create(requestBcb))
            .thenReturn(responseBcb)
        //Ação
        val response = grpcClient.registrar(
            KeyManagerRequest
                .newBuilder()
                .setChave("teste@teste.com")
                .setTipoChave(EMAIL)
                .setTipoConta(CONTA_CORRENTE)
                .setClienteId(titular.id)
                .build()
        )
        //Validação
        assertNotNull(response.pixId)
        assertTrue(repository.existsById(UUID.fromString(response.pixId)))
    }


    @Test
    fun `deve registrar uma chave pix do tipo telefone`(){
        //Cenário
        val pix : Pix = Pix(UUID.fromString(titular.id), TipoChavePix.TELEFONE, "+5547992929292", contaDoCliente.tipo, contaDoCliente.toModel())
        val requestBcb: CreatePixKeyRequest = pix.toCreatePixRequest()
        val responseBcb: CreatePixKeyResponse = CreatePixKeyResponse(
            PixKeyType.by(pix.tipoChave), pix.chave,
            pix.conta?.toBankAccount(pix.tipoConta), pix.conta?.toOwner(), LocalDateTime.now()
        )
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        Mockito.`when`(bancoCentralClient
            .create(requestBcb))
            .thenReturn(responseBcb)
        //Ação
        val response = grpcClient.registrar(
            KeyManagerRequest
                .newBuilder()
                .setChave("+5547992929292")
                .setTipoChave(TELEFONE)
                .setTipoConta(CONTA_CORRENTE)
                .setClienteId(titular.id)
                .build()
        )
        //Validação
        assertNotNull(response.pixId)
        assertTrue(repository.existsById(UUID.fromString(response.pixId)))
    }


    @Test
    fun `nao deve adicionar uma chave que ja existe`(){
        val pix : Pix = Pix(UUID.fromString(titular.id), TipoChavePix.CPF, titular.cpf, contaDoCliente.tipo, contaDoCliente.toModel())
        repository.save(pix)

        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave(titular.cpf)
                    .setTipoChave(CPF)
                    .setTipoConta(CONTA_CORRENTE)
                    .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.ALREADY_EXISTS.code,  status.code)
            assertEquals("Chave já registrada", status.description)
        }
    }



    @Test
    fun `deve gerar erro ao nao encontrar uma conta`(){
        Mockito.`when`(contaClient
            .buscaConta(UUID.randomUUID().toString(), "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave("")
                    .setTipoChave(ALEATORIA)
                    .setTipoConta(CONTA_CORRENTE)
                    .setClienteId(UUID.randomUUID().toString())
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code,  status.code)
            assertEquals("Conta não existe", status.description)
        }
    }

    @ParameterizedTest
    @MethodSource("restricoes")
    fun `deve gerar erro com chave invalida`(tipo: TipoChave, chave: String){
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave(chave)
                    .setTipoChave(tipo)
                    .setTipoConta(CONTA_CORRENTE)
                    .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,  status.code)
            assertEquals("Chave incompatível com o tipo de chave: ${tipo}", status.description)
        }
    }

    @Test
    fun `deve gerar erro com enum chave desconhecida`(){
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave("+5585988714077")
                    .setTipoChave(CHAVE_DESCONHECIDA)
                    .setTipoConta(CONTA_DESCONHECIDA)
                    .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,  status.code)
            assertEquals("Tipo de chave desconhecido.", status.description)
        }
    }

    @Test
    fun `deve gerar erro com enum conta desconhecida`(){
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave("+5585988714077")
                    .setTipoChave(TELEFONE)
                    .setTipoConta(CONTA_DESCONHECIDA)
                    .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,  status.code)
            assertEquals("Tipo de conta desconhecido.", status.description)
        }
    }

    companion object{
        @JvmStatic
        fun restricoes(): List<Arguments> {
            return listOf(
                Arguments.of(CPF, "123"),
                Arguments.of(TELEFONE, "9999999"),
                Arguments.of(EMAIL, "teste.com"),
                Arguments.of(ALEATORIA, "123"))
        }

        @JvmStatic
        fun validos(): List<Arguments> {
            return listOf(
                Arguments.of(CPF, "15593143030", TipoChavePix.CPF),
                Arguments.of(TELEFONE, "+5585988714077", TipoChavePix.TELEFONE),
                Arguments.of(EMAIL, "teste@teste.com.br", TipoChavePix.EMAIL),
                Arguments.of(ALEATORIA, "", TipoChavePix.ALEATORIA));
        }

    }



    @MockBean(ContaClient::class)
    fun contaMock(): ContaClient {
        return Mockito.mock(ContaClient::class.java)
    }

    @MockBean(BancoCentralClient::class)
    fun bancoMock(): BancoCentralClient {
        return Mockito.mock(BancoCentralClient::class.java)
    }


    @Factory
    class GrpcFactory {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub{
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}
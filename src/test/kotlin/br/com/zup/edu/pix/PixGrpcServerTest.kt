package br.com.zup.edu.pix

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoChave.CHAVE_DESCONHECIDA
import br.com.zup.edu.TipoChave.TELEFONE
import br.com.zup.edu.TipoConta.CONTA_CORRENTE
import br.com.zup.edu.TipoConta.CONTA_DESCONHECIDA
import br.com.zup.edu.pix.registra.ContaClient
import io.grpc.Status
import io.grpc.StatusRuntimeException
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
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class PixGrpcServerTest(val repository: PixRepository, val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub){

    @field:Inject
    lateinit var contaClient: ContaClient

//    @field:Inject
//    @field:Client("/")
//    lateinit var client: HttpClient

    val instituicao: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
    val titular: Titular = Titular("c56dfef4-7901-44fb-84e2-a2cefb157890", "Rafael M C Ponte", "02467781054")
    val contaDoCliente: ContaDoCliente = ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao, "0001", "291900", titular)

    @BeforeEach
    fun setup(){

        repository.deleteAll()
    }

    @ParameterizedTest
    @MethodSource("validos")
    fun `deve registrar uma chave pix`(tipo: TipoChave, chave: String){
        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
           contaDoCliente)
        val response = grpcClient.registrar(
            KeyManagerRequest
                .newBuilder()
                .setChave(chave)
                .setTipoChave(tipo)
                .setTipoConta(CONTA_CORRENTE)
                .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .build()
        )
        assertNotNull(response.pixId)
        assertTrue(repository.existsById(UUID.fromString(response.pixId)))
    }

    @Test
    fun `nao deve adicionar uma chave que ja existe`(){
        val existente = repository.save(Pix(br.com.zup.edu.pix.TipoChave.CPF, "15593143030", TipoConta.CONTA_CORRENTE, contaDoCliente.toModel()))

        Mockito.`when`(contaClient
            .buscaConta(titular.id, "CONTA_CORRENTE"))
            .thenReturn(
                contaDoCliente)
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                KeyManagerRequest
                    .newBuilder()
                    .setChave("15593143030")
                    .setTipoChave(TipoChave.CPF)
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
                Arguments.of(TipoChave.CPF, "123"),
                Arguments.of(TELEFONE, "9999999"),
                Arguments.of(TipoChave.EMAIL, "teste.com"),
                Arguments.of(TipoChave.ALEATORIA, "123"))
        }

        @JvmStatic
        fun validos(): List<Arguments> {
            return listOf(
                Arguments.of(TipoChave.CPF, "15593143030"),
                Arguments.of(TELEFONE, "+5585988714077"),
                Arguments.of(TipoChave.EMAIL, "teste@teste.com.br"),
                Arguments.of(TipoChave.ALEATORIA, ""))
        }

    }



    @MockBean(ContaClient::class)
    fun contaMock(): ContaClient {
        return Mockito.mock(ContaClient::class.java)
    }

}
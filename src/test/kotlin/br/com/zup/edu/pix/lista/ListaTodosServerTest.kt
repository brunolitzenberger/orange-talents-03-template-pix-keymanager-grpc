package br.com.zup.edu.pix.lista

import br.com.zup.edu.KeyManagerShowAllServiceGrpc
import br.com.zup.edu.ShowAllPixKeyRequest
import br.com.zup.edu.pix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListaTodosServerTest(
    @Inject val grpcCliente: KeyManagerShowAllServiceGrpc.KeyManagerShowAllServiceBlockingStub,
    @Inject val repository: PixRepository
) {

    @BeforeEach
    fun setup() {
        val instituicao1: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
        val titular1: Titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Yuri Matheus", "86135457004")
        val contaDoCliente1: ContaDoCliente =
            ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao1, "0001", "291900", titular1)
        val pix1: Pix =
            Pix(UUID.fromString(titular1.id), TipoChave.ALEATORIA, "", contaDoCliente1.tipo, contaDoCliente1.toModel())
        repository.save(pix1)

        val instituicao2: Instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
        val titular2: Titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Yuri Matheus", "86135457004")
        val contaDoCliente2: ContaDoCliente =
            ContaDoCliente(TipoConta.CONTA_CORRENTE, instituicao2, "0001", "291900", titular2)
        val pix2: Pix = Pix(
            UUID.fromString(titular1.id),
            TipoChave.CPF,
            "86135457004",
            contaDoCliente2.tipo,
            contaDoCliente2.toModel()
        )
        repository.save(pix2)

    }

    @Test
    fun `deve listar todas as chaves por clienteId`() {
        val request = grpcCliente.all(
            ShowAllPixKeyRequest
                .newBuilder()
                .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b")
                .build()
        )
        assertNotNull(request)
        assertTrue(request.chavePixList.size > 0)
    }


    @Test
    fun `deve retornar erro quando clienteId for vazio`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcCliente.all(
                ShowAllPixKeyRequest
                    .newBuilder()
                    .setClienteId("")
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado.", status.description)
        }

    }

    @Test
    fun `deve retornar uma lista vazia caso o cliente nao tenha chaves registradas`() {
        val request = grpcCliente.all(
            ShowAllPixKeyRequest
                .newBuilder()
                .setClienteId(UUID.randomUUID().toString())
                .build()
        )
        assertNotNull(request)
        assertTrue(request.chavePixList.size == 0)

    }


    @AfterEach
    fun after() {
        repository.deleteAll()
    }

    @Factory
    class GrpcFactory {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerShowAllServiceGrpc.KeyManagerShowAllServiceBlockingStub {
            return KeyManagerShowAllServiceGrpc.newBlockingStub(channel)
        }

    }
}
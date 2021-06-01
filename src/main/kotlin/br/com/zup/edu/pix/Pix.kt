package br.com.zup.edu.pix

import br.com.zup.edu.pix.TipoChave.ALEATORIA
import br.com.zup.edu.pix.bancocentral.registra.CreatePixKeyRequest
import br.com.zup.edu.pix.bancocentral.delete.DeletePixKeyRequest
import br.com.zup.edu.pix.bancocentral.PixKeyType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Pix(

    @Column(columnDefinition = "BINARY(16)")
    var clienteId: UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoChave: TipoChave?,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    var chave: String?,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipoConta: TipoConta?,

    @Embedded
    val conta: ContaAssociada?

) {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null

    var instante: LocalDateTime = LocalDateTime.now()

    init {
        if(tipoChave == ALEATORIA){
            chave = UUID.randomUUID().toString()
        }
    }

    fun toCreatePixRequest(): CreatePixKeyRequest {
        var chave = this.chave
        if(tipoChave == ALEATORIA){
            chave = ""
        }
        return CreatePixKeyRequest(
            keyType = PixKeyType.by(tipoChave),
            key = chave,
            bankAccount = conta?.toBankAccount(tipoConta),
            owner = conta?.toOwner()
        )
    }

    fun validaDono(clienteId: UUID) = this.clienteId.equals(clienteId)

    fun toDeletePixRequest(): DeletePixKeyRequest {
        return DeletePixKeyRequest(chave, "60701190")
    }

    fun atualizaChave(chave: String?){
        if(tipoChave == ALEATORIA){
            this.chave = chave
        }
    }

}

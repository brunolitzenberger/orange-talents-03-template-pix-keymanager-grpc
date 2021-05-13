package br.com.zup.edu.pix

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Pix(
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
    var id: UUID? = null

    var instante: LocalDateTime = LocalDateTime.now()

    init {
        if(tipoChave == TipoChave.ALEATORIA){
            chave = UUID.randomUUID().toString()
        }
    }

}

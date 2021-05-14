package br.com.zup.edu.pix

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class ContaAssociada(
    @field:NotBlank
    @Column(name = "instituicao_conta", nullable = false)
    val instituicao: String,

    @field:NotBlank
    @Column(name = "titular_conta", nullable = false)
    val nomeDoTitular: String,

    @field:NotBlank
    @field:Size(max = 11)
    @Column(name = "cpf_titular_conta", length = 11, nullable = false)
    val cpfDoTitular: String,

    @field:NotBlank
    @field:Size(max = 4)
    @Column(name = "agencia_conta", length = 4, nullable = false)
    val agencia: String,

    @field:NotBlank
    @Column(name = "numero_conta", length = 6, nullable = false)
    val numeroDaConta: String
) {

    fun validaDono(cpf: String?): Boolean{
        return cpf == cpfDoTitular
    }

}

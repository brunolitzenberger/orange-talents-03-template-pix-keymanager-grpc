package br.com.zup.edu.pix

import br.com.zup.edu.pix.bancocentral.registra.AccountType
import br.com.zup.edu.pix.bancocentral.BankAccount
import br.com.zup.edu.pix.bancocentral.Owner
import br.com.zup.edu.pix.bancocentral.registra.OwnerType.NATURAL_PERSON
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


    companion object {
        public val ITAU_UNIBANCO_ISPB: String = "60701190"
    }

    fun toBankAccount(tipoConta: TipoConta?): BankAccount {
        return BankAccount(ITAU_UNIBANCO_ISPB, agencia, numeroDaConta, AccountType.by(tipoConta))
    }

    fun toOwner(): Owner {
        return Owner(NATURAL_PERSON, nomeDoTitular, cpfDoTitular)
    }

}

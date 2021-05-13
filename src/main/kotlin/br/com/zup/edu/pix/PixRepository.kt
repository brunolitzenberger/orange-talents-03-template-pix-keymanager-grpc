package br.com.zup.edu.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface PixRepository : CrudRepository<Pix, UUID> {
    fun existsByChave(chave: String?) :Boolean

}

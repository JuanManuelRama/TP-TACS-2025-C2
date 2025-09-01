package com.g7.repo

import com.g7.usuario.Usuario
import java.util.UUID

interface UsuarioRepo {

    fun getUsuarios(): Set<Usuario>

    fun save(usuario: Usuario)

    fun getUsuarioFromId(id: UUID): Result<Usuario>

    fun getOptionalUsuarioFromUsername(username: String): Usuario?

}

object UsuarioRepository : UsuarioRepo {
    private val usuarios = mutableSetOf<Usuario>()

    override fun getUsuarios(): Set<Usuario> = HashSet(usuarios)
    override fun save(usuario: Usuario) {
        usuarios.add(usuario)
    }

    /**
     * Retorna result en vez de null porque en un futuro el error puede ser de la db, no del pedido
     * */
    override fun getUsuarioFromId(id: UUID): Result<Usuario>  =
        usuarios.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Usuario with id $id not found"))

    override fun getOptionalUsuarioFromUsername(username: String): Usuario? =
        usuarios.find { it.username == username }

}
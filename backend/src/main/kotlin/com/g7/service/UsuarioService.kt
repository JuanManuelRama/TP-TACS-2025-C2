package com.g7.service

import com.g7.repo.Cache
import com.g7.repo.Cache.get
import com.g7.repo.Cache.set
import com.g7.repo.UsuarioRepo
import com.g7.usuario.UnknownUser
import com.g7.usuario.UsuarioResponseDto
import com.g7.usuario.toResponseDto
object UsuarioService {

    fun getUsuarios(ids: Set<String>): Map<String, UsuarioResponseDto> {
        val maps = Cache.mget<UsuarioResponseDto?>(ids)
        val missing = maps.filter { it.value == null }.keys
        val fetched = UsuarioRepo.batchGetFromId(missing)
            .mapValues { it.value.toResponseDto() }
        Cache.mset<UsuarioResponseDto>(fetched)
        val combined = maps.mapValues {
            it.value ?: fetched[it.key] ?: UnknownUser.toResponseDto()
        }
        return combined
    }

    fun getUsuario(id: String): UsuarioResponseDto {
        Cache.get<UsuarioResponseDto>("$id-usuario") ?.let { return it }

        val usuario = UsuarioRepo.getFromId(id)
        Cache.set<UsuarioResponseDto>("$id-usuario", usuario.toResponseDto())
        return usuario.toResponseDto()

    }

}
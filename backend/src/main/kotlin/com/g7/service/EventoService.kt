package com.g7.service

import com.g7.evento.EventoResponseDto
import com.g7.evento.toDto
import com.g7.repo.Cache
import com.g7.repo.Cache.get
import com.g7.repo.Cache.set
import com.g7.repo.EventoRepo


object EventoService {

    fun getEventOwner(id: String): String {
        Cache.get<String>("$id-owner") ?.let { return it }

        val owner = EventoRepo.getOwnerFromId(id)
        Cache.set<String>("$id-owner", owner)

        return owner
    }

    fun getEvento(id: String): EventoResponseDto {
        Cache.get<EventoResponseDto>("$id-evento") ?.let { return it }

        val evento = EventoRepo.getFromId(id)
        val owner = UsuarioService.getUsuario(evento.organizador)
        val eventoDto = evento.toDto(owner)

        Cache.set<EventoResponseDto>("$id-evento", eventoDto)

        return eventoDto
    }

    fun getEventos(ids: Set<String>): Map<String, EventoResponseDto> {
        val maps = Cache.mget<EventoResponseDto?>(ids)
        val missing = maps.filter { it.value == null }.keys
        val fetched = missing.associateWith { id ->
            val evento = EventoRepo.getFromId(id)
            val owner = UsuarioService.getUsuario(evento.organizador)
            evento.toDto(owner)
        }
        Cache.mset<EventoResponseDto>(fetched)
        val combined = maps.mapValues {
            it.value ?: fetched[it.key]!!
        }
        return combined
    }

}
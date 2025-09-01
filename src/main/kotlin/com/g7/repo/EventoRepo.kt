package com.g7.repo

import com.g7.evento.Evento
import com.g7.evento.FiltroEvento
import io.ktor.util.collections.ConcurrentSet
import java.util.HashSet
import java.util.UUID

interface EventoRepo {
    fun getEventos(): Set<Evento>

    fun saveEvento(evento: Evento)

    fun findById(id: UUID): Result<Evento>

    fun getEventosFiltrado(filtro: FiltroEvento): Set<Evento>
}

object EventoRepository : EventoRepo {
    private val eventos = ConcurrentSet<Evento>()

    override fun getEventos(): Set<Evento> = HashSet(eventos)

    override fun saveEvento(evento: Evento) {
        eventos.add(evento)
    }

    /**
     * Retorna Result en vez de simplemente null porque en un futuro el error puede ser
     * propio de la db (falla de conexión)
     * */
    override fun findById(id: UUID): Result<Evento> {
        return eventos.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Evento con id $id no encontrado"))
    }

    override fun getEventosFiltrado(filtro: FiltroEvento): Set<Evento> =
        eventos.filter { filtro.cumple(it) }.toSet()
}
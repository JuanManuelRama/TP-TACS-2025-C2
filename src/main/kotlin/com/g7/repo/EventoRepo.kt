package com.g7.repo

import com.g7.evento.Evento
import com.g7.evento.FiltroFechaAntes
import java.util.HashSet

interface EventoRepo {
    fun getEventos(): Set<Evento>

    fun saveEvento(evento: Evento)

    fun getEventosFiltrado(filtro: FiltroFechaAntes): Set<Evento>
}

object EventoRepository : EventoRepo {
    private val eventos = mutableSetOf<Evento>()

    override fun getEventos(): Set<Evento> = HashSet(eventos)

    override fun saveEvento(evento: Evento) {
        eventos.add(evento)
    }

    override fun getEventosFiltrado(filtro: FiltroFechaAntes): Set<Evento> =
        eventos.filter { filtro.cumple(it) }.toSet()
}
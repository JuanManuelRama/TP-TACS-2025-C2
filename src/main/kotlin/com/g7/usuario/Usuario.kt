package com.g7.usuario

import com.g7.evento.Evento
import java.util.UUID

class Usuario(val id: UUID, val nombre: String) {
    val inscripciones: MutableSet<Evento> = HashSet()
    val esperas: MutableSet<Evento> = HashSet()
    val eventosOrganizados: MutableSet<Evento> = HashSet()

    fun inscribir(evento: Evento, espera: Boolean = false): Result<Unit> {
        if (inscripciones.contains(evento) || esperas.contains(evento)) {
            return Result.failure(RuntimeException("El usuario ya estaba anotado"))
        }
        if (espera) esperas.add(evento) else inscripciones.add(evento)
        return Result.success(Unit)
    }

    fun cancelarInscripcion(evento: Evento): Result<Unit> {
        if (inscripciones.remove(evento) || esperas.remove(evento)) {
            return Result.success(Unit)
        }
        return Result.failure(RuntimeException("El usuario no estaba inscripto"))
    }
}
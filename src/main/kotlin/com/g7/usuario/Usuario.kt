package com.g7.usuario

import com.g7.evento.Evento
import java.util.UUID

class Usuario(val id: UUID, val nombre: String) {
    val inscripciones: MutableSet<Evento> = HashSet()
    val esperas: MutableSet<Evento> = HashSet()
    val eventosOrganizados: MutableSet<Evento> = HashSet()

    /**
     * Registra al usuario en un evento.
     *
     * Si el usuario ya está inscripto o en espera para el evento, devuelve un [Result.failure].
     * En caso contrario, se agrega a la lista de inscripciones o a la lista de espera según
     * el parámetro [espera].
     *
     * @param evento El evento al que se quiere inscribir el usuario.
     * @param espera Si es `true`, el usuario se agrega a la lista de espera; si es `false`,
     *               se agrega directamente a la lista de inscripciones confirmadas.
     * @return [Result.success] si la inscripción fue exitosa, o [Result.failure] con un
     *         [RuntimeException] si el usuario ya estaba anotado.
     */
    fun inscribir(evento: Evento, espera: Boolean = false): Result<Unit> {
        if (evento in inscripciones || evento in esperas) {
            return Result.failure(RuntimeException("El usuario ya estaba anotado"))
        }
        if (espera) esperas.add(evento) else inscripciones.add(evento)
        return Result.success(Unit)
    }

    /**
     * Cancela la inscripción o espera del usuario en un evento.
     *
     * Si el usuario estaba inscrito o en espera para el evento, se elimina de la correspondiente
     * colección y se devuelve un [Result.success]. Si el usuario no estaba anotado, devuelve
     * un [Result.failure].
     *
     * @param evento El evento del cual se desea cancelar la inscripción del usuario.
     * @return [Result.success] si la cancelación se realizó correctamente, o [Result.failure]
     *         con un [RuntimeException] si el usuario no estaba inscrito.
     */
    fun cancelarInscripcion(evento: Evento): Result<Unit> {
        if (inscripciones.remove(evento) || esperas.remove(evento)) {
            return Result.success(Unit)
        }
        return Result.failure(RuntimeException("El usuario no estaba inscripto"))
    }
}
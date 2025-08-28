package com.g7.evento

import com.g7.usuario.Usuario
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Serializable
enum class Categoria {
    FIESTA,
    CONCIERTO,
}
/**
 * @constructor Al instanciar esta clase se registra automáticamente la creación
 * del evento en el usuario correspondiente y se inicializan los contadores internos.
 * */
class Evento(
    val id: UUID,
    val organizador: Usuario,
    val titulo: String,
    val descripcion: String,
    val inicio: LocalDateTime,
    val duracion: Duration,
    val cupoMaximo: Int,
    val cupoMinimio: Int?,
    val precio: Float,
   val categorias: List<Categoria>,
) {
    val inscriptos: MutableSet<Inscripcion.Confirmacion> = HashSet()
    val enEspera: ArrayDeque<Inscripcion.Espera> = ArrayDeque()
    var cantEspera: Int = 0
    var cantEsperaExitosas: Int = 0
    var cantEsperaCancelada: Int = 0

    init { organizador.eventosOrganizados.add(this) }

    fun isFull(): Boolean = inscriptos.size == cupoMaximo

    /**
     * Registra a un usuario en el evento.
     *
     * Si el evento todavía no alcanzó su capacidad máxima, se lo inscribe directamente.
     * En caso contrario, el usuario se agrega a la lista de espera.
     *
     * El método está sincronizado para evitar condiciones de carrera al modificar
     * las listas de inscriptos o de espera.
     *
     * @param usuario El usuario que desea registrarse.
     * @return [Result.success] si la operación fue exitosa (inscripción o espera),
     * o [Result.failure] si ocurrió un error durante el proceso.
     */
    @Synchronized
    fun inscribir(usuario: Usuario): Result<Inscripcion> {
        return if (isFull()) confirmar(usuario) else esperar(usuario)
    }

    /**
     * Cancela la inscripción de un usuario a un evento
     *
     *
     * El método está sincronizado para evitar condiciones de carrera al modificar
     * las listas de inscriptos o de espera.
     *
     * @param usuario El usuario que desea registrarse.
     * @return [Result.success] si la operación fue exitosa (inscripción o espera),
     * o [Result.failure] si ocurrió un error durante el proceso (no estaba inscripto).
     */
    @Synchronized
    fun cancelar(usuario: Usuario): Result<Unit> {
        val resultado = cancelarConfirmacion(usuario)
        return if (resultado.isSuccess) resultado else cancelarEspera(usuario)
    }

    /**
     * Intenta confirmar un usuario en el evento.
     *
     * Si el evento ya alcanzó el cupo máximo, devuelve un [Result.failure].
     * En caso contrario:
     * - Registra la inscripción en el usuario.
     * - Agrega la inscripción al conjunto de inscriptos.
     *
     * @param usuario Usuario que intenta inscribirse.
     * @return [Result.success] si la inscripción fue realizada, o [Result.failure] si no fue posible.
     */
    fun confirmar(usuario: Usuario): Result<Inscripcion> {
        if (this.isFull()) {
            return Result.failure(RuntimeException("No hay espacios disponibles"))
        }
        val confirmacion = Inscripcion.Confirmacion(usuario, LocalDateTime.now(), null)
        return usuario.addInscripcion(this)
            .map {
                inscriptos.add(confirmacion)
                confirmacion
            }
    }

    /**
     * Cancela la inscripción de un usuario en el evento.
     *
     * - Si el usuario no estaba inscripto, devuelve [Result.failure].
     * - Si estaba inscripto, se lo elimina de la lista de inscriptos y se intenta
     *   promover al primer usuario en espera.
     * - En caso de promover a alguien de la lista de espera:
     *   - Se crea una nueva inscripción para ese usuario.
     *   - Se actualizan los contadores de éxito en espera.
     *   - Se ajustan las colecciones de inscripciones y esperas del usuario.
     *
     * @param usuario Usuario que desea cancelar su inscripción.
     * @return [Result.success] si se completó correctamente, o [Result.failure]
     * si el usuario no estaba inscripto.
     */
    fun cancelarConfirmacion(usuario: Usuario): Result<Unit> {
        if (!this.inscriptos.removeIf{i -> i.usuario == usuario}) {
            return Result.failure(RuntimeException("El usuario no estaba inscripto"))
        }
        usuario.removeInscripcion(this).getOrElse { return Result.failure(it) }
        enEspera.removeFirstOrNull()?.let { inscripto ->
            val usuarioInscripto = inscripto.usuario
            val nuevaConfirmacion = inscripto.toConfirmacion()
            inscriptos.add(nuevaConfirmacion)
            cantEsperaExitosas += 1
            usuarioInscripto.esperas.remove(this)
            usuarioInscripto.inscripciones.add(this)
        }
        return Result.success(Unit)
    }

    /**
     * Registra a un usuario en la lista de espera.
     *
     * - Si el evento todavía tiene cupo, devuelve [Result.failure], ya que no corresponde esperar.
     * - Si el usuario ya estaba inscripto o en espera, devuelve [Result.failure].
     * - En caso válido, se crea un objeto [com.g7.evento.Inscripcion.Espera] y se agrega a la cola de espera.
     *
     * @param com.g7.usuario Usuario que desea esperar un lugar en el evento.
     * @return [Result.success] si fue agregado a la lista de espera, o [Result.failure] en caso contrario.
     */
    fun esperar(usuario: Usuario): Result<Inscripcion> {
        if (!this.isFull()) {
            return Result.failure(RuntimeException("Hay espacios disponibles, no debería esperar"))
        }
        if (usuario.anotado(this)) {
            return Result.failure(RuntimeException("El usuario ya estaba anotado"))
        }
        val espera = Inscripcion.Espera(usuario, LocalDateTime.now())
        return usuario.addEspera(this)
            .map {
                enEspera.add(espera)
                cantEspera += 1
                espera
            }
    }

    /**
     * Cancela la espera de un usuario en la cola de espera.
     *
     * - Si el usuario no estaba en la cola, devuelve [Result.failure].
     * - Si estaba, se lo elimina de la lista de espera y se actualizan los contadores de cancelación.
     *
     * @param usuario Usuario que desea cancelar su espera.
     * @return [Result.success] si la cancelación fue realizada, o [Result.failure] si el usuario no estaba en espera.
     */
    fun cancelarEspera(usuario: Usuario): Result<Unit> {
        if (!enEspera.removeIf { i -> i.usuario == usuario }) {
            return Result.failure(RuntimeException("El usuario no estaba en espera"))
        }
        return usuario.removeEspera(this)
            .onSuccess { cantEsperaCancelada += 1 }
    }

    fun porcentajeExito(): Float? = ratio(cantEsperaExitosas, cantEspera)

    fun porcentajeCancelacion(): Float? = ratio(cantEsperaCancelada, cantEspera)

    private fun ratio(part: Int, total: Int): Float? =
        if (total == 0) null else (part.toFloat() / total.toFloat()) * 100
}
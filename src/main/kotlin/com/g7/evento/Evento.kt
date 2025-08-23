package com.g7.evento

import com.g7.usuario.Usuario
import kotlin.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

enum class Categoria {
    FIESTA,
    CONCIERTO,
}
/**
 * @constructor Al instanciar esta clase se registra automáticamente la creación
 * del com.g7.com.g7.evento en el log del sistema y se inicializan los contadores internos.
 * */
class Evento(
    val organizador: Usuario,
    val titulo: String,
    val descripcion: String,
    val fecha: LocalDate,
    val horaInicio: LocalDateTime,
    val duracion: Duration,
    val cupoMaximo: Int,
    val cupoMinimio: Int?,
    val precio: Float,
    val categorias: List<Categoria>,
) {
    val inscriptos: MutableSet<Inscripcion> = HashSet()
    val enEspera: ArrayDeque<Espera> = ArrayDeque()
    var cantEspera: Int = 0
    var cantEsperaExitosas: Int = 0
    var cantEsperaCancelada: Int = 0

    init {
        organizador.eventosOrganizados.add(this)
    }

    fun isFull(): Boolean = inscriptos.size == cupoMaximo

    /**
     * Intenta inscribir un com.g7.com.g7.usuario en el com.g7.com.g7.evento.
     *
     * Si el com.g7.com.g7.evento ya alcanzó el cupo máximo, devuelve un [Result.failure].
     * En caso contrario:
     * - Registra la inscripción en el com.g7.com.g7.usuario.
     * - Agrega la inscripción al conjunto de inscriptos.
     *
     * @param com.g7.usuario Usuario que intenta inscribirse.
     * @return [Result.success] si la inscripción fue realizada, o [Result.failure] si no fue posible.
     */
    @Synchronized
    fun inscribir(usuario: Usuario): Result<Unit> {
        if (this.isFull()) {
            return Result.failure(RuntimeException("No hay espacios disponibles"))
        }
        val inscripcion = Inscripcion(usuario, this, LocalDateTime.now(), null)
        return usuario.addInscripcion(this)
            .onSuccess { inscriptos.add(inscripcion) }
    }

    /**
     * Cancela la inscripción de un com.g7.com.g7.usuario en el com.g7.com.g7.evento.
     *
     * - Si el com.g7.com.g7.usuario no estaba inscripto, devuelve [Result.failure].
     * - Si estaba inscripto, se lo elimina de la lista de inscriptos y se intenta
     *   promover al primer com.g7.com.g7.usuario en espera.
     * - En caso de promover a alguien de la lista de espera:
     *   - Se crea una nueva inscripción para ese com.g7.com.g7.usuario.
     *   - Se actualizan los contadores de éxito en espera.
     *   - Se ajustan las colecciones de inscripciones y esperas del com.g7.com.g7.usuario.
     *
     * @param com.g7.usuario Usuario que desea cancelar su inscripción.
     * @return [Result.success] si se completó correctamente, o [Result.failure] si el com.g7.com.g7.usuario no estaba inscripto.
     */
    @Synchronized
    fun cancelarInscripcion(usuario: Usuario): Result<Unit> {
        if (!this.inscriptos.removeIf{i -> i.usuario == usuario}) {
            return Result.failure(RuntimeException("El com.g7.com.g7.usuario no estaba inscripto"))
        }
        usuario.removeInscripcion(this).getOrElse { return Result.failure(it) }
        enEspera.removeFirstOrNull()?.let { inscripto ->
            val usuarioInscripto = inscripto.usuario
            val nuevaInscripcion = Inscripcion(usuarioInscripto, this, LocalDateTime.now(),
                inscripto.tiempoEsperando())
            inscriptos.add(nuevaInscripcion)
            cantEsperaExitosas += 1
            usuarioInscripto.esperas.remove(this)
            usuarioInscripto.inscripciones.add(this)
        }
        return Result.success(Unit)
    }

    /**
     * Registra a un com.g7.com.g7.usuario en la lista de espera.
     *
     * - Si el com.g7.com.g7.evento todavía tiene cupo, devuelve [Result.failure], ya que no corresponde esperar.
     * - Si el com.g7.com.g7.usuario ya estaba inscripto o en espera, devuelve [Result.failure].
     * - En caso válido, se crea un objeto [Espera] y se agrega a la cola de espera.
     *
     * @param com.g7.usuario Usuario que desea esperar un lugar en el com.g7.com.g7.evento.
     * @return [Result.success] si fue agregado a la lista de espera, o [Result.failure] en caso contrario.
     */
    @Synchronized
    fun esperar(usuario: Usuario): Result<Unit> {
        if (!this.isFull()) {
            return Result.failure(RuntimeException("Hay espacios disponibles, no debería esperar"))
        }
        if (usuario.anotado(this)) {
            return Result.failure(RuntimeException("El com.g7.com.g7.usuario ya estaba anotado"))
        }
        val espera = Espera(usuario, this, LocalDateTime.now())
        return usuario.addEspera(this)
            .onSuccess {
                enEspera.add(espera)
                cantEspera += 1
            }
    }

    /**
     * Cancela la espera de un com.g7.com.g7.usuario en la cola de espera.
     *
     * - Si el com.g7.com.g7.usuario no estaba en la cola, devuelve [Result.failure].
     * - Si estaba, se lo elimina de la lista de espera y se actualizan los contadores de cancelación.
     *
     * @param com.g7.usuario Usuario que desea cancelar su espera.
     * @return [Result.success] si la cancelación fue realizada, o [Result.failure] si el com.g7.com.g7.usuario no estaba en espera.
     */
    @Synchronized
    fun cancelarEspera(usuario: Usuario): Result<Unit> {
        if (!enEspera.removeIf { i -> i.usuario == usuario }) {
            return Result.failure(RuntimeException("El com.g7.com.g7.usuario no estaba en espera"))
        }
        return usuario.removeEspera(this)
            .onSuccess { cantEsperaCancelada += 1 }
    }

    fun porcentajeExito(): Float? = ratio(cantEsperaExitosas, cantEspera)

    fun porcentajeCancelacion(): Float? = ratio(cantEsperaCancelada, cantEspera)

    private fun ratio(part: Int, total: Int): Float? =
        total.takeIf { it > 0 }?.let { (part.toFloat() / it) * 100 }
}
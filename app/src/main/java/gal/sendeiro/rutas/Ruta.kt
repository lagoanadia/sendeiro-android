package gal.sendeiro.rutas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ruta(
    @PrimaryKey val id: Long,
    val nombre: String,
    val km: Double,
    val cerrada: Boolean
)

// Treinta rutas de prueba; una de cada cuatro está cerrada
fun rutasDePrueba(): List<Ruta> = List(30) { i ->
    Ruta(id = i + 1L, nombre = "Ruta de prueba ${i + 1}", km = 3.0 + i * 1.5, cerrada = i % 4 == 0)
}
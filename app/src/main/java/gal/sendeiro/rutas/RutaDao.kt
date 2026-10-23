package gal.sendeiro.rutas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RutaDao {
    @Query("SELECT * FROM Ruta ORDER BY id")
    suspend fun todas(): List<Ruta>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(rutas: List<Ruta>)

    @Delete
    suspend fun borrar(ruta: Ruta)

    @Query("DELETE FROM Ruta")
    suspend fun borrarTodas()
        @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ruta: Ruta)
}
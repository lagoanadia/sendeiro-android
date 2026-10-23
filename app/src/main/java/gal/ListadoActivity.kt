package gal.sendeiro.rutas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Estado { CARGANDO, DATOS, VACIO, ERROR }

class ListadoActivity : AppCompatActivity() {

    private lateinit var lista: RecyclerView
    private lateinit var estadoDatos: View
    private lateinit var estadoCargando: View
    private lateinit var estadoVacio: View
    private lateinit var estadoError: View
    private lateinit var checkMillas: CheckBox
    private var adaptador: Adaptador? = null
    private val prefs by lazy { getSharedPreferences("ajustes", MODE_PRIVATE) }
    private val dao by lazy { BaseDatos.obtener(this).rutaDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        lista = findViewById(R.id.lista)
        estadoDatos = findViewById(R.id.estadoDatos)
        estadoCargando = findViewById(R.id.estadoCargando)
        estadoVacio = findViewById(R.id.estadoVacio)
        estadoError = findViewById(R.id.estadoError)
        checkMillas = findViewById(R.id.checkMillas)
        lista.layoutManager = LinearLayoutManager(this)

        // Ajuste en preferencias: se lee al abrir y se guarda al marcar
        checkMillas.isChecked = prefs.getBoolean("millas", false)
        checkMillas.setOnCheckedChangeListener { _, marcado ->
            prefs.edit().putBoolean("millas", marcado).apply()
            adaptador?.let { it.millas = marcado; it.notifyDataSetChanged() }
        }

        // «Descargar» (simulado): guarda las rutas en Room y recarga
        findViewById<Button>(R.id.botonDescargar).setOnClickListener {
            lifecycleScope.launch {
                dao.insertarTodas(rutasDePrueba())
                cargar()
            }
        }
        findViewById<Button>(R.id.botonReintentar).setOnClickListener { cargar() }

        // Para probar: adb shell am start -S ... --es estado vacio|error
        cargar(intent.getStringExtra("estado"))
    }

    // Las consultas son suspend: Room las ejecuta fuera del hilo principal
    private fun cargar(forzado: String? = null) {
        lifecycleScope.launch {
            mostrar(Estado.CARGANDO)
            delay(1500)   // simula la espera
            if (forzado == "error") { mostrar(Estado.ERROR); return@launch }
            if (forzado == "vacio") dao.borrarTodas()

            val rutas = dao.todas()
            if (rutas.isEmpty()) {
                mostrar(Estado.VACIO)
            } else {
                adaptador = Adaptador(
                    rutas.toMutableList(),
                    prefs.getBoolean("millas", false),
                    alBorrar = { ruta -> lifecycleScope.launch { dao.borrar(ruta) } },
                    alPulsar = { ruta ->
                        // Intent explícito: decimos exactamente a qué pantalla vamos
                        startActivity(
                            Intent(this@ListadoActivity, DetalleActivity::class.java)
                                .putExtra("nombre", ruta.nombre)
                                .putExtra("km", ruta.km)
                                .putExtra("cerrada", ruta.cerrada)
                        )
                    }
                )
                lista.adapter = adaptador
                mostrar(Estado.DATOS)
            }
        }
    }

    private fun mostrar(estado: Estado) {
        estadoCargando.visibility = if (estado == Estado.CARGANDO) View.VISIBLE else View.GONE
        estadoDatos.visibility = if (estado == Estado.DATOS) View.VISIBLE else View.GONE
        estadoVacio.visibility = if (estado == Estado.VACIO) View.VISIBLE else View.GONE
        estadoError.visibility = if (estado == Estado.ERROR) View.VISIBLE else View.GONE
    }
}
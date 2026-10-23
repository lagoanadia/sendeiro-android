package gal.sendeiro.rutas

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

enum class Estado { CARGANDO, DATOS, VACIO, ERROR }

class ListadoActivity : AppCompatActivity() {

    private lateinit var lista: RecyclerView
    private lateinit var estadoCargando: View
    private lateinit var estadoVacio: View
    private lateinit var estadoError: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        lista = findViewById(R.id.lista)
        estadoCargando = findViewById(R.id.estadoCargando)
        estadoVacio = findViewById(R.id.estadoVacio)
        estadoError = findViewById(R.id.estadoError)
        lista.layoutManager = LinearLayoutManager(this)

        // Para probar los estados: adb shell am start ... --es estado vacio|error
        val simulado = intent.getStringExtra("estado") ?: "datos"
        findViewById<Button>(R.id.botonDescargar).setOnClickListener { cargar("datos") }
        findViewById<Button>(R.id.botonReintentar).setOnClickListener { cargar("datos") }
        cargar(simulado)
    }

    // Simula una carga de 1,5 segundos y luego enseña el resultado
    private fun cargar(resultado: String) {
        mostrar(Estado.CARGANDO)
        Handler(Looper.getMainLooper()).postDelayed({
            when (resultado) {
                "vacio" -> mostrar(Estado.VACIO)
                "error" -> mostrar(Estado.ERROR)
                else -> {
                    lista.adapter = Adaptador(rutasDePrueba().toMutableList())
                    mostrar(Estado.DATOS)
                }
            }
        }, 1500)
    }

    private fun mostrar(estado: Estado) {
        estadoCargando.visibility = if (estado == Estado.CARGANDO) View.VISIBLE else View.GONE
        lista.visibility = if (estado == Estado.DATOS) View.VISIBLE else View.GONE
        estadoVacio.visibility = if (estado == Estado.VACIO) View.VISIBLE else View.GONE
        estadoError.visibility = if (estado == Estado.ERROR) View.VISIBLE else View.GONE
    }
}
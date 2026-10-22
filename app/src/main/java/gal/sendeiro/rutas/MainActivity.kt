package gal.sendeiro.rutas

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity




/**
 * Sendeiro · pantalla principal (PMDM-UD2, actividad 1).
 *
 * Trazas: ventana Logcat de Android Studio, con el filtro  tag:CICLO
 *         o en una terminal:  adb logcat -c && adb logcat -s CICLO:D
 *
 * Qué enseña: las siete llamadas del ciclo de vida de una pantalla, y qué sobrevive y qué no
 * cuando el sistema la destruye y la vuelve a crear (al girar el móvil, por ejemplo).
 *  - Lo escrito en los dos campos SOBREVIVE solo: Android guarda el estado de las vistas con id.
 *  - El contador «creaciones» NO: es una variable de este objeto, y el objeto nuevo nace a cero.
 *    Hacer que sobreviva es la parte de «tocar» de la actividad.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "CICLO"                       // un solo distintivo, para filtrar el registro
    private lateinit var campoNombre: EditText      // lateinit: «se rellenará en onCreate»
    private lateinit var campoDistancia: EditText
    private lateinit var textoCreaciones: TextView
    private var creaciones = 0                      // cuántas veces se ha creado esta pantalla

    /** La llama el sistema cada vez que la pantalla se CREA: la primera vez y al reconstruirla. */
    override fun onCreate(savedInstanceState: Bundle?) {   // nulo la primera vez; lleno si vuelve
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)      // qué fichero de res/layout pinta esta pantalla
        val como = if (savedInstanceState == null) "nulo" else "restaurado"
        Log.d(TAG, "onCreate · estado $como")

        campoNombre = findViewById(R.id.campoNombre)        // la vista del layout con ese id
        campoDistancia = findViewById(R.id.campoDistancia)
        textoCreaciones = findViewById(R.id.textoCreaciones)

        creaciones = creaciones + 1                 // TOCAR: hoy siempre da 1. ¿Por qué?
        textoCreaciones.text = "Esta pantalla se ha creado $creaciones veces"
    }

    override fun onStart()   { super.onStart();   Log.d(TAG, "onStart") }     // ya se ve
    override fun onResume()  { super.onResume();  Log.d(TAG, "onResume") }    // ya se puede tocar
    override fun onPause()   { super.onPause();   Log.d(TAG, "onPause") }     // pierde el foco
    override fun onStop()    { super.onStop();    Log.d(TAG, "onStop") }      // ya no se ve
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }   // vuelve sin destruirse
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }   // este objeto se acaba

    /** La llama el sistema ANTES de destruir la pantalla, por si luego hay que reconstruirla. */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)         // guarda sola lo escrito en las vistas con id
        Log.d(TAG, "onSaveInstanceState")
        // TOCAR: guardar aquí el contador, y recuperarlo en onCreate.
    }
}

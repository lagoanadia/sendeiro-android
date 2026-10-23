package gal.sendeiro.rutas

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView




/**
 * Sendeiro · pantalla principal (PMDM-UD2, actividad 1).
 *
 * Compilar:  ./gradlew.bat assembleDebug   (en Linux o Mac: ./gradlew)
 * Instalar:  ./gradlew.bat installDebug    (instala en todo lo que salga en adb devices)
 * Hay que tener antes:
 *  - JDK 17 y el SDK de Android (variable ANDROID_HOME, o sdk.dir en local.properties,
 *    que no se versiona).
 *  - Un emulador arrancado (minimo, API 24, u objetivo, API 37) o un móvil con
 *    depuración USB. Comprobarlo con adb devices: tiene que salir como "device".
 * Trazas:    adb logcat -c && adb logcat -s CICLO:D
 *
 * Qué enseña: las siete llamadas del ciclo de vida y qué sobrevive y qué no cuando
 * el sistema destruye la pantalla (al girar, por ejemplo). El contador «creaciones»
 * sobrevive guardándolo en el Bundle de onSaveInstanceState.
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
        val campoTramo: AutoCompleteTextView = findViewById(R.id.campoTramo)
        campoTramo.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.tramos)))

        creaciones = (savedInstanceState?.getInt("creaciones") ?: 0) + 1          // TOCAR: hoy siempre da 1. ¿Por qué?
       textoCreaciones.text = getString(R.string.creaciones, creaciones)
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
        outState.putInt("creaciones", creaciones)   // guarda el contador en el Bundle
    }
}


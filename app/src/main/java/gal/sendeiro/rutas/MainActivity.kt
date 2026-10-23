package gal.sendeiro.rutas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

/**
 * Sendeiro · pantalla principal (PMDM-UD2, actividades 1 y 2).
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
 * Qué hace: alta de ruta con validación en tres momentos (al escribir, al salir del
 * campo y al guardar). El botón Guardar está desactivado hasta que todo sea válido.
 * El borrador (SharedPreferences) solo guarda los campos válidos.
 * El contador «creaciones» sobrevive guardándolo en el Bundle de onSaveInstanceState.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "CICLO"
    private lateinit var campoNombre: EditText
    private lateinit var campoDistancia: EditText
    private lateinit var campoTramo: AutoCompleteTextView
    private lateinit var campoDescripcion: EditText
    private lateinit var grupoDificultad: RadioGroup
    private lateinit var checkCircular: CheckBox
    private lateinit var botonGuardar: Button
    private lateinit var textoCreaciones: TextView
    private var creaciones = 0
    private val prefs by lazy { getSharedPreferences("borrador", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val como = if (savedInstanceState == null) "nulo" else "restaurado"
        Log.d(TAG, "onCreate · estado $como")

        campoNombre = findViewById(R.id.campoNombre)
        campoDistancia = findViewById(R.id.campoDistancia)
        campoTramo = findViewById(R.id.campoTramo)
        campoDescripcion = findViewById(R.id.campoDescripcion)
        grupoDificultad = findViewById(R.id.grupoDificultad)
        checkCircular = findViewById(R.id.checkCircular)
        botonGuardar = findViewById(R.id.botonGuardar)
        textoCreaciones = findViewById(R.id.textoCreaciones)

        creaciones = (savedInstanceState?.getInt("creaciones") ?: 0) + 1
        textoCreaciones.text = getString(R.string.creaciones, creaciones)

        campoTramo.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.tramos))
        )

        // Momento 1 · al escribir: pista suave y botón al día
        campoNombre.doAfterTextChanged { actualizarBoton() }
        campoDistancia.doAfterTextChanged { pistaDistancia(); actualizarBoton() }
        campoTramo.doAfterTextChanged { actualizarBoton() }
        grupoDificultad.setOnCheckedChangeListener { _, _ -> actualizarBoton() }

        // Momento 2 · al salir del campo: mensaje de error claro
        campoNombre.setOnFocusChangeListener { _, tieneFoco -> if (!tieneFoco) validarNombre() }
        campoDistancia.setOnFocusChangeListener { _, tieneFoco -> if (!tieneFoco) validarDistancia() }
        campoTramo.setOnFocusChangeListener { _, tieneFoco -> if (!tieneFoco) validarTramo() }

        // Momento 3 · al guardar: la validación de verdad
        botonGuardar.setOnClickListener {
            if (validarTodo()) guardar() else primerFallo()?.requestFocus()
        }

        // Borrador: solo se restaura si la pantalla se abre de cero
        if (savedInstanceState == null) restaurarBorrador()

        actualizarBoton()
    }

    // ---- reglas (sin tocar la pantalla) ----
    private fun nombreOk() = campoNombre.text.toString().trim().length >= 3

    private fun distanciaValor(): Double? =
        campoDistancia.text.toString().trim().replace(',', '.').toDoubleOrNull()

    private fun distanciaOk(): Boolean {
        val d = distanciaValor()
        return d != null && d > 0 && d <= 500
    }

    private fun dificultadOk() = grupoDificultad.checkedRadioButtonId != -1

    private fun tramoOk() =
        resources.getStringArray(R.array.tramos).contains(campoTramo.text.toString())

    private fun todoOk() = nombreOk() && distanciaOk() && dificultadOk() && tramoOk()

    // ---- validaciones que sí enseñan el error ----
    private fun validarNombre(): Boolean {
        val ok = nombreOk()
        campoNombre.error = if (ok) null else getString(R.string.error_nombre)
        return ok
    }

    private fun validarDistancia(): Boolean {
        val vacio = campoDistancia.text.toString().isBlank()
        val ok = distanciaOk()
        campoDistancia.error = when {
            ok -> null
            vacio -> getString(R.string.error_distancia_vacia)
            else -> getString(R.string.error_distancia_invalida)
        }
        return ok
    }

    private fun validarDificultad(): Boolean {
        val ok = dificultadOk()
        if (!ok) Toast.makeText(this, R.string.error_dificultad, Toast.LENGTH_SHORT).show()
        return ok
    }

    private fun validarTramo(): Boolean {
        val ok = tramoOk()
        campoTramo.error = if (ok) null else getString(R.string.error_tramo)
        return ok
    }

    private fun validarTodo(): Boolean {
        val a = validarNombre()
        val b = validarDistancia()
        val c = validarDificultad()
        val d = validarTramo()
        return a && b && c && d
    }

    private fun primerFallo(): View? = when {
        !nombreOk() -> campoNombre
        !distanciaOk() -> campoDistancia
        !dificultadOk() -> grupoDificultad
        !tramoOk() -> campoTramo
        else -> null
    }

    private fun pistaDistancia() {
        val texto = campoDistancia.text.toString().trim()
        val raro = texto.isNotEmpty() && distanciaValor() == null
        campoDistancia.error = if (raro) getString(R.string.pista_numero) else null
    }

    private fun actualizarBoton() {
        botonGuardar.isEnabled = todoOk()
    }

    private fun guardar() {
        prefs.edit().clear().apply()   // ya no hace falta el borrador
        Toast.makeText(this, R.string.ruta_guardada, Toast.LENGTH_SHORT).show()
    }

    // ---- borrador: solo se guarda lo que es válido ----
    private fun guardarBorrador() {
        prefs.edit()
            .putString("nombre", if (nombreOk()) campoNombre.text.toString().trim() else "")
            .putString("distancia", if (distanciaOk()) campoDistancia.text.toString().trim() else "")
            .putInt("dificultad", grupoDificultad.checkedRadioButtonId)
            .putBoolean("circular", checkCircular.isChecked)
            .putString("tramo", if (tramoOk()) campoTramo.text.toString() else "")
            .putString("descripcion", campoDescripcion.text.toString())
            .apply()
    }

    private fun restaurarBorrador() {
        campoNombre.setText(prefs.getString("nombre", ""))
        campoDistancia.setText(prefs.getString("distancia", ""))
        val dificultad = prefs.getInt("dificultad", -1)
        if (dificultad != -1) grupoDificultad.check(dificultad)
        checkCircular.isChecked = prefs.getBoolean("circular", false)
        campoTramo.setText(prefs.getString("tramo", ""), false)
        campoDescripcion.setText(prefs.getString("descripcion", ""))
    }

    // ---- ciclo de vida ----
    override fun onStart()   { super.onStart();   Log.d(TAG, "onStart") }
    override fun onResume()  { super.onResume();  Log.d(TAG, "onResume") }
    override fun onPause()   { super.onPause();   Log.d(TAG, "onPause"); guardarBorrador() }
    override fun onStop()    { super.onStop();    Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putInt("creaciones", creaciones)
    }
}
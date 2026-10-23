package gal.sendeiro.rutas

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class DetalleActivity : AppCompatActivity() {

    private val permiso = Manifest.permission.ACCESS_FINE_LOCATION

    // Android nos devuelve aquí la respuesta del usuario al diálogo del permiso
    private val pedirPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        when {
            concedido -> usarPosicion()                                  // camino 1: concedido
            shouldShowRequestPermissionRationale(permiso) -> explicar()  // camino 2: denegado
            else -> denegadoParaSiempre()                                // camino 3: denegado para siempre
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val km = intent.getDoubleExtra("km", 0.0)
        val cerrada = intent.getBooleanExtra("cerrada", false)

        findViewById<TextView>(R.id.detalleNombre).text = nombre
        findViewById<TextView>(R.id.detalleKm).text = getString(R.string.km_formato, km)
        findViewById<TextView>(R.id.detalleCerrada).visibility = if (cerrada) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.botonCompartir).setOnClickListener { compartir(nombre, km) }

        // El permiso se pide EN CONTEXTO: solo cuando el usuario pulsa el botón
        findViewById<Button>(R.id.botonPosicion).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED) {
                usarPosicion()
            } else {
                pedirPermiso.launch(permiso)
            }
        }
    }

    private fun usarPosicion() {
        findViewById<TextView>(R.id.detallePosicion).text = getString(R.string.posicion_concedida)
    }

    // Camino 2: se explica para qué sirve y se puede volver a pedir
    private fun explicar() {
        Snackbar.make(raiz(), R.string.permiso_explicacion, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.permiso_reintentar) { pedirPermiso.launch(permiso) }
            .show()
    }

    // Camino 3: ya no se puede pedir; enlace a ajustes y la app sigue funcionando sin la función
    private fun denegadoParaSiempre() {
        Snackbar.make(raiz(), R.string.permiso_denegado_siempre, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.boton_ajustes) {
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null))
                )
            }
            .show()
    }

    private fun raiz(): View = findViewById(R.id.detalleRaiz)

    // Intent implícito: no decimos qué app, decimos qué queremos hacer
    private fun compartir(nombre: String, km: Double) {
        val envio = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.parte_texto, nombre, km))
        }
        try {
            startActivity(Intent.createChooser(envio, getString(R.string.compartir_titulo)))
            Snackbar.make(raiz(), R.string.parte_compartido, Snackbar.LENGTH_SHORT).show()
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(raiz(), R.string.sin_app_compartir, Snackbar.LENGTH_LONG).show()
        }
    }
}
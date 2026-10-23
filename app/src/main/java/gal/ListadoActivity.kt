package gal.sendeiro.rutas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)
        val lista: RecyclerView = findViewById(R.id.lista)
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = Adaptador(rutasDePrueba())
    }
}
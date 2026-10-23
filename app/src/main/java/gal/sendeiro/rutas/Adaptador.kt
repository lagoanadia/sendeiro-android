package gal.sendeiro.rutas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adaptador(private val rutas: List<Ruta>) : RecyclerView.Adapter<Adaptador.Fila>() {

    private var llamadasCreate = 0
    private var llamadasBind = 0

    class Fila(v: View) : RecyclerView.ViewHolder(v) {
        val nombre: TextView = v.findViewById(R.id.nombre)
        val km: TextView = v.findViewById(R.id.km)
        val etiqueta: TextView = v.findViewById(R.id.etiqueta)
        val botonBorrar: ImageButton = v.findViewById(R.id.botonBorrar)
    }

    override fun getItemCount() = rutas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Fila {
        llamadasCreate++
        Log.d("RECICLADO", "onCreateViewHolder · llamadas: $llamadasCreate")
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ruta, parent, false)
        return Fila(v)
    }

    override fun onBindViewHolder(f: Fila, posicion: Int) {
        llamadasBind++
        Log.d("RECICLADO", "onBindViewHolder · posición $posicion · llamadas: $llamadasBind")
        val r = rutas[posicion]
        f.nombre.text = r.nombre
        f.km.text = f.itemView.context.getString(R.string.km_formato, r.km)
        if (r.cerrada) f.etiqueta.visibility = View.VISIBLE
        else f.etiqueta.visibility = View.GONE
    }
}
package com.mantenimiento.soportetecnicoapp.vistas

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.OrdenServicioEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemOrdenBinding
import java.text.SimpleDateFormat
import java.util.*

class OrdenAdapter(private val ordenes: List<OrdenServicioEntity>) :
    RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder>() {

    class OrdenViewHolder(val binding: ItemOrdenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val binding = ItemOrdenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val orden = ordenes[position]
        holder.binding.tvOrdenFalla.text = orden.Falla_reportada
        holder.binding.tvOrdenEstado.text = "Estado: ${orden.Estado}"
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.binding.tvOrdenFecha.text = "Fecha: ${sdf.format(Date(orden.Fecha_ingreso))}"

        orden.Foto_path?.let { path ->
            holder.binding.ivOrdenFoto.setImageURI(Uri.fromFile(java.io.File(path)))
        } ?: run {
            holder.binding.ivOrdenFoto.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    override fun getItemCount(): Int = ordenes.size
}

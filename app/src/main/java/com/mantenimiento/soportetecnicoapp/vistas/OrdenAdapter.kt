package com.mantenimiento.soportetecnicoapp.vistas

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.OrdenServicioEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemOrdenBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class OrdenAdapter(
    private var dataList: List<OrdenConCliente>,
    private val onItemClick: ((OrdenServicioEntity) -> Unit)? = null
) : RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder>() {

    data class OrdenConCliente(
        val orden: OrdenServicioEntity,
        val cliente: ClienteEntity?
    )

    class OrdenViewHolder(val binding: ItemOrdenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val binding = ItemOrdenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val item = dataList[position]
        val orden = item.orden
        val cliente = item.cliente

        holder.binding.tvOrdenFalla.text = orden.Falla_reportada
        holder.binding.tvOrdenCliente.text = cliente?.let { "Dueño: ${it.Nombre_cl} (${it.Telefono})" } ?: "Sin dueño"
        holder.binding.tvOrdenEstado.text = "Estado: ${orden.Estado}"
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.binding.tvOrdenFecha.text = "Fecha: ${sdf.format(Date(orden.Fecha_ingreso))}"

        orden.Foto_path?.let { path ->
            holder.binding.ivOrdenFoto.setImageURI(Uri.fromFile(File(path)))
        } ?: run {
            holder.binding.ivOrdenFoto.setImageResource(android.R.drawable.ic_menu_camera)
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(orden)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newList: List<OrdenConCliente>) {
        this.dataList = newList
        notifyDataSetChanged()
    }
}

package com.mantenimiento.soportetecnicoapp.vistas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.RepuestoOrdenEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemRepuestoBinding

class RepuestoAdapter(private var list: List<RepuestoOrdenEntity>) :
    RecyclerView.Adapter<RepuestoAdapter.RepuestoViewHolder>() {

    class RepuestoViewHolder(val binding: ItemRepuestoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepuestoViewHolder {
        val binding = ItemRepuestoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepuestoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepuestoViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvRepuestoNombre.text = "${item.Cantidad}x ${item.Nombre_repuesto}"
        holder.binding.tvRepuestoPrecio.text = "S/. ${String.format("%.2f", item.Precio_unitario * item.Cantidad)}"
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RepuestoOrdenEntity>) {
        this.list = newList
        notifyDataSetChanged()
    }
}

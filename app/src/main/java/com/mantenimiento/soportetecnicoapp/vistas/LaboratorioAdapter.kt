package com.mantenimiento.soportetecnicoapp.vistas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.EstadoComponentesDesarmeEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemLaboratorioBinding

class LaboratorioAdapter(private var items: List<EstadoComponentesDesarmeEntity>) :
    RecyclerView.Adapter<LaboratorioAdapter.LabViewHolder>() {

    class LabViewHolder(val binding: ItemLaboratorioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding = ItemLaboratorioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvEquipoLab.text = "Equipo ID: ${item.Id_eq}"
        holder.binding.tvPantallaStatus.text = "Pantalla: ${item.La_Pantalla ?: "N/A"}"
        holder.binding.tvPlacaStatus.text = "Placa: ${item.La_Placa_Madre ?: "N/A"}"
        holder.binding.tvBateriaStatus.text = "Batería: ${item.La_Bateria ?: "N/A"}"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<EstadoComponentesDesarmeEntity>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

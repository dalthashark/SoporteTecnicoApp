package com.mantenimiento.soportetecnicoapp.vistas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.EstadoComponentesDesarmeEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemLaboratorioBinding

class LaboratorioAdapter(
    private var items: List<LabItemData>,
    private val onEditClick: (EstadoComponentesDesarmeEntity) -> Unit
) : RecyclerView.Adapter<LaboratorioAdapter.LabViewHolder>() {

    data class LabItemData(
        val ecd: EstadoComponentesDesarmeEntity,
        val equipo: EquipoClienteEntity?
    )

    class LabViewHolder(val binding: ItemLaboratorioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding = ItemLaboratorioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        val item = items[position]
        val ecd = item.ecd
        val eq = item.equipo

        holder.binding.tvEquipoLab.text = eq?.let { "${it.Tipo_equipo} ${it.Modelo}" } ?: "Equipo ID: ${ecd.Id_eq}"
        holder.binding.tvPantallaStatus.text = "Pantalla: ${ecd.La_Pantalla ?: "N/A"}"
        holder.binding.tvPlacaStatus.text = "Placa: ${ecd.La_Placa_Madre ?: "N/A"}"
        holder.binding.tvBateriaStatus.text = "Batería: ${ecd.La_Bateria ?: "N/A"}"

        holder.binding.btnEditarLab.setOnClickListener {
            onEditClick(ecd)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: List<LabItemData>) {
        this.items = newList
        notifyDataSetChanged()
    }
}

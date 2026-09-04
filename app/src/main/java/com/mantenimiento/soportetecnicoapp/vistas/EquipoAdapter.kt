package com.mantenimiento.soportetecnicoapp.vistas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemEquipoBinding

class EquipoAdapter(
    private val equipos: List<EquipoClienteEntity>,
    private val onEditClick: ((EquipoClienteEntity) -> Unit)? = null
) : RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder>() {

    class EquipoViewHolder(val binding: ItemEquipoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val binding = ItemEquipoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EquipoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        val equipo = equipos[position]
        holder.binding.tvEquipoNombre.text = "${equipo.Tipo_equipo} ${equipo.Modelo}"
        holder.binding.tvEquipoSerie.text = "Serie: ${equipo.Num_serie}"
        
        holder.binding.tvEquipoProcesador.text = "CPU: ${equipo.Procesador ?: "N/A"}"
        holder.binding.tvEquipoRAM.text = "RAM: ${equipo.RAM ?: "N/A"}"
        holder.binding.tvEquipoBateria.text = "Batería: ${equipo.Estado_Bateria ?: "N/A"}"

        holder.binding.btnEditarEquipo.setOnClickListener {
            onEditClick?.invoke(equipo)
        }
    }

    override fun getItemCount(): Int = equipos.size
}

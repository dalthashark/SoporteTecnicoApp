package com.mantenimiento.soportetecnicoapp.vistas

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.TecnicoEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemTecnicoBinding
import java.io.File

class TecnicoAdapter(
    private var tecnicos: List<TecnicoEntity>,
    private val onEditClick: (TecnicoEntity) -> Unit,
    private val onDeleteClick: (TecnicoEntity) -> Unit
) : RecyclerView.Adapter<TecnicoAdapter.TecnicoViewHolder>() {

    class TecnicoViewHolder(val binding: ItemTecnicoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TecnicoViewHolder {
        val binding = ItemTecnicoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TecnicoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TecnicoViewHolder, position: Int) {
        val t = tecnicos[position]
        holder.binding.tvNombreTecnicoItem.text = t.Nombre_t
        holder.binding.tvTipoTecnicoItem.text = "Tipo: ${t.Tipo_trabajador}"
        holder.binding.tvInstitutoItem.text = "Institución: ${t.Instituto ?: "N/A"}"
        holder.binding.tvDiasItem.text = "Días: ${t.Dias_trabajo ?: "No definido"}"
        
        val entrada = t.Hora_entrada ?: "--:--"
        val salida = t.Hora_salida ?: "--:--"
        holder.binding.tvHorarioItem.text = "Horario: $entrada - $salida"

        t.Foto_path?.let { path ->
            holder.binding.ivFotoTecnicoItem.setImageURI(Uri.fromFile(File(path)))
        } ?: run {
            holder.binding.ivFotoTecnicoItem.setImageResource(android.R.drawable.ic_menu_camera)
        }

        holder.binding.btnEditarTecnico.setOnClickListener { onEditClick(t) }
        holder.binding.btnEliminarTecnico.setOnClickListener { onDeleteClick(t) }
    }

    override fun getItemCount(): Int = tecnicos.size

    fun updateData(newTecnicos: List<TecnicoEntity>) {
        this.tecnicos = newTecnicos
        notifyDataSetChanged()
    }
}

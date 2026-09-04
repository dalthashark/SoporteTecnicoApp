package com.mantenimiento.soportetecnicoapp.vistas

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.AsistenciaEntity
import com.mantenimiento.soportetecnicoapp.data.entity.TecnicoEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemAsistenciaBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AsistenciaAdapter(
    private var data: List<AsistenciaData>,
    private val onStatusChange: (AsistenciaData) -> Unit,
    private val onCapturePhoto: (AsistenciaData) -> Unit
) : RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder>() {

    data class AsistenciaData(
        val tecnico: TecnicoEntity,
        var asistenciaHoy: AsistenciaEntity? = null,
        var historialSemanal: List<AsistenciaEntity> = emptyList()
    )

    class AsistenciaViewHolder(val binding: ItemAsistenciaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaViewHolder {
        val binding = ItemAsistenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AsistenciaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AsistenciaViewHolder, position: Int) {
        val item = data[position]
        holder.binding.tvNombreAsistencia.text = item.tecnico.Nombre_t

        // Aplicar estado inicial de hoy
        applyStatusUi(holder, item.asistenciaHoy?.estado)
        
        // Cargar historial visual (puntos de colores)
        renderHistorialVisual(holder.binding.layoutHistorialSemanal, item.historialSemanal)

        // Motivo y Foto de hoy si existen
        item.asistenciaHoy?.let { asis ->
            holder.binding.etMotivo.setText(asis.observacion)
            asis.foto_justificacion_path?.let { path ->
                holder.binding.ivFotoJustificacion.setImageURI(Uri.fromFile(File(path)))
            }
        }

        holder.binding.rbAsistio.setOnClickListener { 
            updateItemStatus(item, "ASISTIÓ")
            applyStatusUi(holder, "ASISTIÓ")
            onStatusChange(item)
        }
        holder.binding.rbFalta.setOnClickListener { 
            updateItemStatus(item, "FALTA")
            applyStatusUi(holder, "FALTA")
            onStatusChange(item)
        }
        holder.binding.rbJustificado.setOnClickListener { 
            updateItemStatus(item, "JUSTIFICADO")
            applyStatusUi(holder, "JUSTIFICADO")
            onStatusChange(item)
        }

        holder.binding.etMotivo.addTextChangedListener {
            item.asistenciaHoy?.let { asis ->
                item.asistenciaHoy = asis.copy(observacion = it.toString())
            }
        }

        holder.binding.btnCapturaJustificacion.setOnClickListener {
            onCapturePhoto(item)
        }

        holder.binding.btnGuardarFicha.setOnClickListener {
            onStatusChange(item)
        }
    }

    private fun renderHistorialVisual(layout: LinearLayout, historial: List<AsistenciaEntity>) {
        layout.removeAllViews()
        val context = layout.context
        val sdf = SimpleDateFormat("EE", Locale.getDefault()) // "Lun", "Mar", etc.

        // Mostrar últimos 7 días
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6) // Empezar hace 6 días

        for (i in 0..6) {
            val dayLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(8, 0, 8, 0)
            }

            val dayLabel = TextView(context).apply {
                text = sdf.format(calendar.time).first().toString() // "L", "M", etc.
                textSize = 10f
                gravity = android.view.Gravity.CENTER
            }

            // Buscar asistencia para esta fecha exacta (normalizada a las 00:00)
            val dateRef = calendar.timeInMillis / (1000 * 60 * 60 * 24)
            val asisDia = historial.find { (it.fecha / (1000 * 60 * 60 * 24)) == dateRef }

            val indicator = View(context).apply {
                val size = (12 * context.resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply { topMargin = 4 }
                
                val drawable = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(when (asisDia?.estado) {
                        "ASISTIÓ" -> Color.parseColor("#4CAF50")
                        "FALTA" -> Color.parseColor("#F44336")
                        "JUSTIFICADO" -> Color.parseColor("#FFC107")
                        else -> Color.parseColor("#BDBDBD") // Gris si no hay registro
                    })
                }
                background = drawable
            }

            dayLayout.addView(dayLabel)
            dayLayout.addView(indicator)
            layout.addView(dayLayout)
            
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun updateItemStatus(item: AsistenciaData, nuevoEstado: String) {
        if (item.asistenciaHoy == null) {
            item.asistenciaHoy = AsistenciaEntity(id_tecnico = item.tecnico.Id_t, fecha = 0, estado = nuevoEstado)
        } else {
            item.asistenciaHoy = item.asistenciaHoy?.copy(estado = nuevoEstado)
        }
    }

    private fun applyStatusUi(holder: AsistenciaViewHolder, estado: String?) {
        when (estado) {
            "ASISTIÓ" -> {
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rbAsistio.isChecked = true
            }
            "FALTA" -> {
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rbFalta.isChecked = true
            }
            "JUSTIFICADO" -> {
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(Color.parseColor("#FFFDE7"))
                holder.binding.layoutJustificacion.visibility = View.VISIBLE
                holder.binding.btnGuardarFicha.visibility = View.VISIBLE
                holder.binding.rbJustificado.isChecked = true
            }
            else -> {
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(Color.WHITE)
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rgEstado.clearCheck()
            }
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newList: List<AsistenciaData>) {
        this.data = newList
        notifyDataSetChanged()
    }
}

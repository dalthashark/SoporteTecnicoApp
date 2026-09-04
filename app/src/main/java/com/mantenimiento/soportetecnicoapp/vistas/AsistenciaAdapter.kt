package com.mantenimiento.soportetecnicoapp.vistas

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.R
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
        renderHistorialVisual(holder.binding.layoutHistorialSemanal, item)

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

    private fun renderHistorialVisual(layout: LinearLayout, item: AsistenciaData) {
        layout.removeAllViews()
        val context = layout.context
        val historial = item.historialSemanal
        val diasProgramados = item.tecnico.Dias_trabajo ?: ""

        // Configurar para que empiece el Lunes de la semana actual
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Mapa de abreviaturas en español
        val diasMap = mapOf(
            Calendar.MONDAY to "LU",
            Calendar.TUESDAY to "MA",
            Calendar.WEDNESDAY to "MI",
            Calendar.THURSDAY to "JU",
            Calendar.FRIDAY to "VI",
            Calendar.SATURDAY to "SA",
            Calendar.SUNDAY to "DO"
        )
        
        // Mapa para verificar con Dias_trabajo (que usa "Lun", "Mar", etc)
        val checkMap = mapOf(
            Calendar.MONDAY to "Lun",
            Calendar.TUESDAY to "Mar",
            Calendar.WEDNESDAY to "Mié",
            Calendar.THURSDAY to "Jue",
            Calendar.FRIDAY to "Vie",
            Calendar.SATURDAY to "Sáb",
            Calendar.SUNDAY to "Dom"
        )

        for (i in 0..6) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val esDiaLaboral = diasProgramados.contains(checkMap[dayOfWeek] ?: "")

            val dayLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(8, 0, 8, 0)
            }

            val dayLabel = TextView(context).apply {
                text = diasMap[dayOfWeek]
                textSize = 10f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, if (isDarkMode(context)) R.color.white else R.color.black))
                
                // Resaltar el día de hoy con negrita
                val hoyCal = Calendar.getInstance()
                if (hoyCal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    textSize = 11f
                }
            }

            val dateRef = calendar.timeInMillis / (1000 * 60 * 60 * 24)
            val asisDia = historial.find { (it.fecha / (1000 * 60 * 60 * 24)) == dateRef }

            val indicator = View(context).apply {
                val size = (12 * context.resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply { topMargin = 4 }
                
                val drawable = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    
                    val color = when {
                        asisDia?.estado == "ASISTIÓ" -> Color.parseColor("#4CAF50") // Verde
                        asisDia?.estado == "FALTA" -> Color.parseColor("#F44336") // Rojo
                        asisDia?.estado == "JUSTIFICADO" -> Color.parseColor("#FFC107") // Amarillo
                        !esDiaLaboral -> Color.parseColor("#BDBDBD") // Gris (No le toca venir)
                        else -> Color.parseColor("#E0E0E0") // Gris muy claro (Día laboral pendiente)
                    }
                    setColor(color)
                    
                    // Si no le toca venir, le ponemos un borde o menos opacidad para diferenciar
                    if (!esDiaLaboral && asisDia == null) {
                        alpha = 150
                    }
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
        val context = holder.itemView.context
        val isDark = isDarkMode(context)
        
        // Ajustar color de texto de los RadioButtons para visibilidad
        val textColor = if (isDark) Color.WHITE else Color.BLACK
        holder.binding.rbAsistio.setTextColor(textColor)
        holder.binding.rbFalta.setTextColor(textColor)
        holder.binding.rbJustificado.setTextColor(textColor)

        when (estado) {
            "ASISTIÓ" -> {
                val color = ContextCompat.getColor(context, if (isDark) R.color.asis_asistio_dark else R.color.asis_asistio_light)
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(color)
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rbAsistio.isChecked = true
            }
            "FALTA" -> {
                val color = ContextCompat.getColor(context, if (isDark) R.color.asis_falta_dark else R.color.asis_falta_light)
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(color)
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rbFalta.isChecked = true
            }
            "JUSTIFICADO" -> {
                val color = ContextCompat.getColor(context, if (isDark) R.color.asis_justificado_dark else R.color.asis_justificado_light)
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(color)
                holder.binding.layoutJustificacion.visibility = View.VISIBLE
                holder.binding.btnGuardarFicha.visibility = View.VISIBLE
                holder.binding.rbJustificado.isChecked = true
            }
            else -> {
                holder.binding.cardAsistenciaItem.setCardBackgroundColor(ContextCompat.getColor(context, if (isDark) R.color.surface_dark_slate else R.color.surface_light))
                holder.binding.layoutJustificacion.visibility = View.GONE
                holder.binding.btnGuardarFicha.visibility = View.GONE
                holder.binding.rgEstado.clearCheck()
            }
        }
    }

    private fun isDarkMode(context: android.content.Context): Boolean {
        return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newList: List<AsistenciaData>) {
        this.data = newList
        notifyDataSetChanged()
    }
}

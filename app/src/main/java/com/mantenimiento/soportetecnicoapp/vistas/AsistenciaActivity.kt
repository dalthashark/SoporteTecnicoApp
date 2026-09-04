package com.mantenimiento.soportetecnicoapp.vistas

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.AsistenciaEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityAsistenciaBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AsistenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsistenciaBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: AsistenciaAdapter
    
    private var itemParaFoto: AsistenciaAdapter.AsistenciaData? = null
    private var currentPhotoPath: String? = null
    private var fotoUri: Uri? = null
    
    private var calendarSeleccionado = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    private var jobCarga: Job? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            itemParaFoto?.let { data ->
                val asis = data.asistenciaHoy ?: AsistenciaEntity(
                    id_tecnico = data.tecnico.Id_t, 
                    fecha = calendarSeleccionado.timeInMillis, 
                    estado = "JUSTIFICADO"
                )
                data.asistenciaHoy = asis.copy(foto_justificacion_path = currentPhotoPath)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar Navegación de Fecha
        binding.btnFechaAnterior.setOnClickListener { cambiarFecha(-1) }
        binding.btnFechaSiguiente.setOnClickListener { cambiarFecha(1) }
        binding.btnCalendario.setOnClickListener { mostrarDatePicker() }

        adapter = AsistenciaAdapter(
            emptyList(),
            onStatusChange = { data -> guardarAsistencia(data) },
            onCapturePhoto = { data -> 
                itemParaFoto = data
                prepararCamara()
            }
        )
        binding.rvAsistencia.layoutManager = LinearLayoutManager(this)
        binding.rvAsistencia.adapter = adapter

        actualizarUIFecha()
    }

    private fun cambiarFecha(dias: Int) {
        calendarSeleccionado.add(Calendar.DAY_OF_YEAR, dias)
        actualizarUIFecha()
    }

    private fun mostrarDatePicker() {
        val dpd = DatePickerDialog(this, { _, year, month, day ->
            calendarSeleccionado.set(year, month, day)
            actualizarUIFecha()
        }, calendarSeleccionado.get(Calendar.YEAR), calendarSeleccionado.get(Calendar.MONTH), calendarSeleccionado.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun actualizarUIFecha() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaStr = sdf.format(calendarSeleccionado.time)
        binding.tvFechaHoy.text = fechaStr

        // Determinar etiqueta (Hoy, Ayer, etc)
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val diff = ((calendarSeleccionado.timeInMillis - hoy.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
        
        binding.tvEtiquetaHoy.text = when (diff) {
            0 -> "Hoy"
            -1 -> "Ayer"
            -2 -> "Anteayer"
            else -> if (diff > 0) "Próximo" else "Registro"
        }
        
        cargarDatos()
    }

    private fun prepararCamara() {
        val photoFile: File? = try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JUST_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (ex: Exception) {
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
            fotoUri = photoURI
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun cargarDatos() {
        jobCarga?.cancel() // Cancelar carga anterior si existe
        val fechaBusqueda = calendarSeleccionado.timeInMillis
        
        // Obtener el día de la semana actual para filtrar (Lun, Mar, etc.)
        val checkMap = mapOf(
            Calendar.MONDAY to "Lun",
            Calendar.TUESDAY to "Mar",
            Calendar.WEDNESDAY to "Mié",
            Calendar.THURSDAY to "Jue",
            Calendar.FRIDAY to "Vie",
            Calendar.SATURDAY to "Sáb",
            Calendar.SUNDAY to "Dom"
        )
        val diaNombreHoy = checkMap[calendarSeleccionado.get(Calendar.DAY_OF_WEEK)] ?: ""
        
        jobCarga = lifecycleScope.launch {
            db.tecnicoDao().getAll().collect { listaTecnicos ->
                // FILTRO: Solo mostrar personal que tenga programado este día de la semana
                val tecnicosFiltrados = listaTecnicos.filter { 
                    it.Dias_trabajo?.contains(diaNombreHoy) == true 
                }
                
                val listaData = mutableListOf<AsistenciaAdapter.AsistenciaData>()
                
                for (tecnico in tecnicosFiltrados) {
                    db.asistenciaDao().getHistorialPorTecnico(tecnico.Id_t).collect { historial ->
                        val asisHoy = historial.find { it.fecha == fechaBusqueda }
                        
                        // Historial de la semana relativa a la fecha seleccionada
                        val calSemana = calendarSeleccionado.clone() as Calendar
                        calSemana.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        val inicioSemana = calSemana.timeInMillis
                        val finSemana = inicioSemana + (7 * 24 * 60 * 60 * 1000)
                        
                        val semanal = historial.filter { it.fecha in inicioSemana until finSemana }
                        
                        val existingIndex = listaData.indexOfFirst { it.tecnico.Id_t == tecnico.Id_t }
                        val dataItem = AsistenciaAdapter.AsistenciaData(tecnico, asisHoy, semanal)
                        
                        if (existingIndex != -1) {
                            listaData[existingIndex] = dataItem
                        } else {
                            listaData.add(dataItem)
                        }
                        
                        adapter.updateData(listaData.toList())
                    }
                }
                
                // Si no hay nadie para este día, limpiar la lista
                if (tecnicosFiltrados.isEmpty()) {
                    adapter.updateData(emptyList())
                }
            }
        }
    }

    private fun guardarAsistencia(data: AsistenciaAdapter.AsistenciaData) {
        val asis = data.asistenciaHoy ?: return
        val fechaFinal = calendarSeleccionado.timeInMillis
        lifecycleScope.launch {
            db.asistenciaDao().insert(asis.copy(fecha = fechaFinal))
            Toast.makeText(this@AsistenciaActivity, "Registro guardado para el ${SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(fechaFinal))}", Toast.LENGTH_SHORT).show()
        }
    }
}

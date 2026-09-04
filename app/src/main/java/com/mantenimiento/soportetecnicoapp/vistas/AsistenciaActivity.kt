package com.mantenimiento.soportetecnicoapp.vistas

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
import kotlinx.coroutines.flow.combine
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

    private val hoy: Long by lazy {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            itemParaFoto?.let { data ->
                val asis = data.asistenciaHoy ?: AsistenciaEntity(id_tecnico = data.tecnico.Id_t, fecha = hoy, estado = "JUSTIFICADO")
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

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvFechaHoy.text = "Fecha: ${sdf.format(Date(hoy))}"

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
        lifecycleScope.launch {
            // Obtener todos los técnicos
            db.tecnicoDao().getAll().collect { listaTecnicos ->
                val listaData = mutableListOf<AsistenciaAdapter.AsistenciaData>()
                
                for (tecnico in listaTecnicos) {
                    // Para cada técnico, obtener su historial completo para filtrar hoy y la semana
                    db.asistenciaDao().getHistorialPorTecnico(tecnico.Id_t).collect { historial ->
                        val asisHoy = historial.find { it.fecha == hoy }
                        
                        // Últimos 7 días para el visual
                        val inicioSemana = hoy - (7 * 24 * 60 * 60 * 1000)
                        val semanal = historial.filter { it.fecha >= inicioSemana }
                        
                        // Actualizar o añadir a la lista
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
            }
        }
    }

    private fun guardarAsistencia(data: AsistenciaAdapter.AsistenciaData) {
        val asis = data.asistenciaHoy ?: return
        lifecycleScope.launch {
            db.asistenciaDao().insert(asis.copy(fecha = hoy))
            Toast.makeText(this@AsistenciaActivity, "Registro guardado", Toast.LENGTH_SHORT).show()
        }
    }
}

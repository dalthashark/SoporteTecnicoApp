package com.mantenimiento.soportetecnicoapp.vistas

import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.TecnicoEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityRegistroTecnicoBinding
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegistroTecnicoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroTecnicoBinding
    private lateinit var db: AppDatabase
    
    private var fotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var tecnicoIdParaEditar: Int = -1
    
    private var horaEntrada: String? = null
    private var horaSalida: String? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            fotoUri?.let { binding.ivFotoTecnico.setImageURI(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroTecnicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar Combo Box (Exposed Dropdown Menu)
        val opciones = arrayOf("Trabajador", "Practicante")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)
        binding.acTipoTecnico.setAdapter(spinnerAdapter)

        binding.btnTomarFotoTecnico.setOnClickListener { prepararCamara() }
        binding.btnGuardarTecnico.setOnClickListener { guardarTecnico() }
        
        binding.btnHoraEntrada.setOnClickListener { mostrarTimePicker(true) }
        binding.btnHoraSalida.setOnClickListener { mostrarTimePicker(false) }

        // Verificar si es edición
        tecnicoIdParaEditar = intent.getIntExtra("TECNICO_ID", -1)
        if (tecnicoIdParaEditar != -1) {
            cargarDatosParaEditar()
        }
    }

    private fun mostrarTimePicker(esEntrada: Boolean) {
        val cal = Calendar.getInstance()
        val timePicker = TimePickerDialog(this, { _, hour, minute ->
            val timeStr = String.format("%02d:%02d", hour, minute)
            if (esEntrada) {
                horaEntrada = timeStr
                binding.btnHoraEntrada.text = "ENTRADA: $timeStr"
            } else {
                horaSalida = timeStr
                binding.btnHoraSalida.text = "SALIDA: $timeStr"
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        timePicker.show()
    }

    private fun cargarDatosParaEditar() {
        lifecycleScope.launch {
            val tecnico = db.tecnicoDao().getById(tecnicoIdParaEditar)
            tecnico?.let { t ->
                binding.etNombreTecnico.setText(t.Nombre_t)
                binding.etTelefonoTecnico.setText(t.Telefono)
                binding.etInstituto.setText(t.Instituto)
                binding.etCarrera.setText(t.Carrera)
                
                binding.acTipoTecnico.setText(t.Tipo_trabajador, false)
                
                t.Foto_path?.let { path ->
                    binding.ivFotoTecnico.setImageURI(Uri.fromFile(File(path)))
                    currentPhotoPath = path
                }
                
                // Cargar días seleccionados
                t.Dias_trabajo?.let { dias ->
                    binding.cbLunes.isChecked = dias.contains("Lun")
                    binding.cbMartes.isChecked = dias.contains("Mar")
                    binding.cbMiercoles.isChecked = dias.contains("Mié")
                    binding.cbJueves.isChecked = dias.contains("Jue")
                    binding.cbViernes.isChecked = dias.contains("Vie")
                    binding.cbSabado.isChecked = dias.contains("Sáb")
                    binding.cbDomingo.isChecked = dias.contains("Dom")
                }
                
                t.Hora_entrada?.let {
                    horaEntrada = it
                    binding.btnHoraEntrada.text = "ENTRADA: $it"
                }
                t.Hora_salida?.let {
                    horaSalida = it
                    binding.btnHoraSalida.text = "SALIDA: $it"
                }
                
                binding.btnGuardarTecnico.text = "ACTUALIZAR PERSONAL"
                binding.toolbar.title = "Editar Personal"
            }
        }
    }

    private fun prepararCamara() {
        val photoFile: File? = try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("TECH_${timeStamp}_", ".jpg", storageDir).apply {
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

    private fun guardarTecnico() {
        val nombre = binding.etNombreTecnico.text.toString().trim()
        val telefono = binding.etTelefonoTecnico.text.toString().trim()
        val instituto = binding.etInstituto.text.toString().trim()
        val carrera = binding.etCarrera.text.toString().trim()
        val tipo = binding.acTipoTecnico.text.toString()
        
        // Obtener días seleccionados
        val diasList = mutableListOf<String>()
        if (binding.cbLunes.isChecked) diasList.add("Lun")
        if (binding.cbMartes.isChecked) diasList.add("Mar")
        if (binding.cbMiercoles.isChecked) diasList.add("Mié")
        if (binding.cbJueves.isChecked) diasList.add("Jue")
        if (binding.cbViernes.isChecked) diasList.add("Vie")
        if (binding.cbSabado.isChecked) diasList.add("Sáb")
        if (binding.cbDomingo.isChecked) diasList.add("Dom")
        val diasStr = if (diasList.isEmpty()) "No definido" else diasList.joinToString(", ")

        // Validación: Teléfono obligatorio y exactamente 9 dígitos
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (telefono.isEmpty() || telefono.length != 9) {
            binding.etTelefonoTecnico.error = "El teléfono debe tener 9 dígitos"
            Toast.makeText(this, "El número de teléfono debe ser de 9 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val tecnico = TecnicoEntity(
                Id_t = if (tecnicoIdParaEditar != -1) tecnicoIdParaEditar else 0,
                Nombre_t = nombre,
                Telefono = telefono,
                Especialidad = "General",
                Instituto = instituto,
                Carrera = carrera,
                Tipo_trabajador = tipo,
                Foto_path = currentPhotoPath,
                Dias_trabajo = diasStr,
                Hora_entrada = horaEntrada,
                Hora_salida = horaSalida
            )
            
            if (tecnicoIdParaEditar == -1) {
                db.tecnicoDao().insert(tecnico)
                Toast.makeText(this@RegistroTecnicoActivity, "Personal registrado", Toast.LENGTH_SHORT).show()
            } else {
                db.tecnicoDao().update(tecnico)
                Toast.makeText(this@RegistroTecnicoActivity, "Personal actualizado", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}

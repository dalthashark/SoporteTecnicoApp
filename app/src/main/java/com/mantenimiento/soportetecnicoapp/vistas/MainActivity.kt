package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.OrdenServicioEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityMainBinding
import com.mantenimiento.soportetecnicoapp.db.RestApiClient
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var fotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    // Launcher para tomar foto real con la cámara
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            fotoUri?.let {
                binding.ivFotoEquipo.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnTomarFoto.setOnClickListener {
            prepararCamara()
        }

        binding.btnGuardar.setOnClickListener {
            procesarRegistroTransaccional()
        }
    }

    private fun prepararCamara() {
        val photoFile: File? = try {
            crearArchivoImagen()
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al crear archivo", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            fotoUri = photoURI
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun crearArchivoImagen(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun procesarRegistroTransaccional() {
        val clienteNom = binding.etCliente.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val tipoDisp = binding.etTipoDispositivo.text.toString().trim()
        val equipo = binding.etEquipo.text.toString().trim()
        val falla = binding.etFalla.text.toString().trim()

        if (clienteNom.isNotEmpty() && telefono.isNotEmpty() && tipoDisp.isNotEmpty() && 
            equipo.isNotEmpty() && falla.isNotEmpty()) {

            lifecycleScope.launch {
                try {
                    // 1. Insertar Cliente
                    val idCliente = db.clienteDao().insert(
                        ClienteEntity(
                            Nombre_cl = clienteNom,
                            Telefono = telefono,
                            Tipo_doc = "S/D",
                            Num_doc = "SIN DOCUMENTO",
                            Correo = "no@correo.com"
                        )
                    ).toInt()

                    // 2. Insertar Equipo
                    val idEquipo = db.equipoClienteDao().insert(
                        EquipoClienteEntity(
                            Id_cl = idCliente,
                            Id_m = 1,
                            Tipo_equipo = tipoDisp,
                            Modelo = equipo,
                            Num_serie = "SN-NEW",
                            Características = "Orden Técnica",
                            Estado_propiedad = "Cliente"
                        )
                    ).toInt()

                    // 3. Insertar Orden con Foto Real
                    db.ordenServicioDao().insert(
                        OrdenServicioEntity(
                            Id_eq = idEquipo,
                            Id_t = null,
                            Fecha_ingreso = System.currentTimeMillis(),
                            Falla_reportada = falla,
                            Diagnostico = "Pendiente",
                            Estado = "Recibido",
                            Costo_mano_obra = 0.0,
                            Fecha_entrega = null,
                            Foto_path = currentPhotoPath // Guardamos la ruta absoluta del archivo
                        )
                    )

                    // Enviar al servidor (simulado)
                    RestApiClient.enviarTicketAlServidor(clienteNom, telefono, falla)

                    Toast.makeText(this@MainActivity, "¡Orden técnica guardada!", Toast.LENGTH_LONG).show()
                    limpiarFormulario()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarFormulario() {
        binding.etCliente.setText("")
        binding.etTelefono.setText("")
        binding.etTipoDispositivo.setText("")
        binding.etEquipo.setText("")
        binding.etFalla.setText("")
        binding.ivFotoEquipo.setImageResource(android.R.drawable.ic_menu_camera)
        fotoUri = null
        currentPhotoPath = null
    }
}

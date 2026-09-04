package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
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
    
    private var listaClientes: List<ClienteEntity> = emptyList()
    private var clienteSeleccionado: ClienteEntity? = null

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
        
        binding.btnNuevoClienteRapido.setOnClickListener {
            startActivity(Intent(this, RegistroClienteActivity::class.java))
        }

        configurarSelectores()
    }

    private fun configurarSelectores() {
        // Selector de Clientes
        lifecycleScope.launch {
            db.clienteDao().getAll().collect { clientes ->
                // FILTRO: No mostrar el almacén en el selector de clientes
                listaClientes = clientes.filter { it.Nombre_cl != "DATALAB (ALMACÉN)" }
                val nombres = listaClientes.map { it.Nombre_cl }
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, nombres)
                binding.acSeleccionarCliente.setAdapter(adapter)
            }
        }

        binding.acSeleccionarCliente.setOnItemClickListener { _, _, position, _ ->
            val nombreSeleccionado = binding.acSeleccionarCliente.adapter.getItem(position).toString()
            clienteSeleccionado = listaClientes.find { it.Nombre_cl == nombreSeleccionado }
        }

        // Selector de Tipo de Dispositivo (Solo PC y Laptop)
        val tipos = arrayOf("PC", "Laptop")
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_list_item_1, tipos)
        binding.acTipoDispositivo.setAdapter(adapterTipos)
        binding.acTipoDispositivo.setText(tipos[0], false)
    }

    private fun prepararCamara() {
        val photoFile: File? = try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al crear archivo", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", it)
            fotoUri = photoURI
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun procesarRegistroTransaccional() {
        val tipoDisp = binding.acTipoDispositivo.text.toString()
        val equipoMod = binding.etEquipo.text.toString().trim()
        val falla = binding.etFalla.text.toString().trim()

        if (clienteSeleccionado != null && equipoMod.isNotEmpty() && falla.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val idCliente = clienteSeleccionado!!.Id_cl

                    // 1. Insertar Equipo
                    val idEquipo = db.equipoClienteDao().insert(
                        EquipoClienteEntity(
                            Id_cl = idCliente,
                            Id_m = 1,
                            Tipo_equipo = tipoDisp,
                            Modelo = equipoMod,
                            Num_serie = "S/N",
                            Características = "Pendiente de revisión detallada",
                            Estado_propiedad = "Cliente"
                        )
                    ).toInt()

                    // 2. Insertar Orden
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
                            Foto_path = currentPhotoPath
                        )
                    )

                    RestApiClient.enviarTicketAlServidor(clienteSeleccionado!!.Nombre_cl, clienteSeleccionado!!.Telefono, falla)

                    Toast.makeText(this@MainActivity, "¡Ingreso registrado!", Toast.LENGTH_LONG).show()
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Seleccione cliente y complete el equipo/falla", Toast.LENGTH_SHORT).show()
        }
    }
}

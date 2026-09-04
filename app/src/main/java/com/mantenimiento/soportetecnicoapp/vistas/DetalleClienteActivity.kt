package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.databinding.ActivityDetalleClienteBinding
import kotlinx.coroutines.launch

class DetalleClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleClienteBinding
    private var clienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)

        if (clienteId != -1) {
            cargarDatosCliente()
        }
    }

    private fun cargarDatosCliente() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val cliente = db.clienteDao().getById(clienteId)
            cliente?.let {
                binding.tvNombreDetalle.text = it.Nombre_cl
                binding.tvDocDetalle.text = "${it.Tipo_doc}: ${it.Num_doc}"
                binding.tvTelefonoDetalle.text = "Teléfono: ${it.Telefono}"
                binding.tvCorreoDetalle.text = "Correo: ${it.Correo}"
            }

            db.equipoClienteDao().getAll().collect { listaEquipos ->
                val equiposFiltrados = listaEquipos.filter { it.Id_cl == clienteId }
                binding.rvEquiposCliente.layoutManager = LinearLayoutManager(this@DetalleClienteActivity)
                binding.rvEquiposCliente.adapter = EquipoAdapter(equiposFiltrados)
                
                // Cargar órdenes de esos equipos
                val idsEquipos = equiposFiltrados.map { it.Id_eq }
                db.ordenServicioDao().getAll().collect { listaOrdenes ->
                    val ordenesFiltradas = listaOrdenes.filter { it.Id_eq in idsEquipos }
                    binding.rvOrdenesCliente.layoutManager = LinearLayoutManager(this@DetalleClienteActivity)
                    binding.rvOrdenesCliente.adapter = OrdenAdapter(ordenesFiltradas)
                }
            }
        }
    }
}

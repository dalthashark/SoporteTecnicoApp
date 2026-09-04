package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityListaClientesBinding
import kotlinx.coroutines.launch

class ListaClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaClientesBinding
    private lateinit var adapter: ClienteAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = ClienteAdapter(
            emptyList(),
            onItemClick = { cliente ->
                val intent = Intent(this, DetalleClienteActivity::class.java)
                intent.putExtra("CLIENTE_ID", cliente.Id_cl)
                startActivity(intent)
            },
            onEditClick = { cliente ->
                val intent = Intent(this, RegistroClienteActivity::class.java)
                intent.putExtra("CLIENTE_ID", cliente.Id_cl)
                startActivity(intent)
            },
            onDeleteClick = { cliente ->
                confirmarEliminacion(cliente)
            }
        )
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter

        cargarClientes()
    }

    private fun confirmarEliminacion(cliente: ClienteEntity) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de eliminar a ${cliente.Nombre_cl}? Se eliminarán también sus equipos y órdenes.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    db.clienteDao().delete(cliente)
                    Toast.makeText(this@ListaClientesActivity, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarClientes() {
        lifecycleScope.launch {
            db.clienteDao().getAll().collect { clientes ->
                // FILTRO: Solo ocultamos el registro que dice exactamente "DATALAB (ALMACÉN)"
                val soloClientesReales = clientes.filter { 
                    it.Nombre_cl != "DATALAB (ALMACÉN)" 
                }
                adapter.updateData(soloClientesReales)
            }
        }
    }
}

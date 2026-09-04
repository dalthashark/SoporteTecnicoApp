package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.databinding.ActivityListaClientesBinding
import kotlinx.coroutines.launch

class ListaClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaClientesBinding
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = ClienteAdapter(emptyList()) { cliente ->
            val intent = Intent(this, DetalleClienteActivity::class.java)
            intent.putExtra("CLIENTE_ID", cliente.Id_cl)
            startActivity(intent)
        }
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter

        cargarClientes()
    }

    private fun cargarClientes() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.clienteDao().getAll().collect { clientes ->
                adapter.updateData(clientes)
            }
        }
    }
}

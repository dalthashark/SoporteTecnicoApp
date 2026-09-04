package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.databinding.ActivityLaboratorioBinding
import kotlinx.coroutines.launch

class LaboratorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaboratorioBinding
    private lateinit var adapter: LaboratorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaboratorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = LaboratorioAdapter(emptyList())
        binding.rvLaboratorio.layoutManager = LinearLayoutManager(this)
        binding.rvLaboratorio.adapter = adapter

        cargarLaboratorio()
    }

    private fun cargarLaboratorio() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.estadoComponentesDesarmeDao().getAll().collect { lista ->
                adapter.updateData(lista)
            }
        }
    }
}

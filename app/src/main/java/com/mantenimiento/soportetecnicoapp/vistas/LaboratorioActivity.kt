package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.databinding.ActivityLaboratorioBinding
import kotlinx.coroutines.flow.combine
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

        adapter = LaboratorioAdapter(emptyList()) { item ->
            val intent = Intent(this, RegistroLaboratorioActivity::class.java)
            intent.putExtra("ECD_ID", item.Id_ecd)
            startActivity(intent)
        }
        binding.rvLaboratorio.layoutManager = LinearLayoutManager(this)
        binding.rvLaboratorio.adapter = adapter

        cargarLaboratorio()
    }

    private fun cargarLaboratorio() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            // Combinar los datos de componentes con los datos del equipo para mostrar el nombre
            combine(
                db.estadoComponentesDesarmeDao().getAll(),
                db.equipoClienteDao().getAll()
            ) { listaEcd, listaEq ->
                listaEcd.map { ecd ->
                    val equipo = listaEq.find { it.Id_eq == ecd.Id_eq }
                    LaboratorioAdapter.LabItemData(ecd, equipo)
                }
            }.collect { listaData ->
                adapter.updateData(listaData)
            }
        }
    }
}

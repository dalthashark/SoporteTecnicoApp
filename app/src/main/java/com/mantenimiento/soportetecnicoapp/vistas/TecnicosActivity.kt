package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.TecnicoEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityTecnicosBinding
import kotlinx.coroutines.launch

class TecnicosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTecnicosBinding
    private lateinit var adapter: TecnicoAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTecnicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar RecyclerView
        adapter = TecnicoAdapter(
            emptyList(),
            onEditClick = { tecnico ->
                val intent = Intent(this, RegistroTecnicoActivity::class.java)
                intent.putExtra("TECNICO_ID", tecnico.Id_t)
                startActivity(intent)
            },
            onDeleteClick = { tecnico ->
                confirmarEliminacion(tecnico)
            }
        )
        binding.rvTecnicos.layoutManager = LinearLayoutManager(this)
        binding.rvTecnicos.adapter = adapter

        cargarTecnicos()
    }

    private fun confirmarEliminacion(tecnico: TecnicoEntity) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Personal")
            .setMessage("¿Estás seguro de eliminar a ${tecnico.Nombre_t}?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    db.tecnicoDao().delete(tecnico)
                    Toast.makeText(this@TecnicosActivity, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarTecnicos() {
        lifecycleScope.launch {
            db.tecnicoDao().getAll().collect { lista ->
                adapter.updateData(lista)
            }
        }
    }
}

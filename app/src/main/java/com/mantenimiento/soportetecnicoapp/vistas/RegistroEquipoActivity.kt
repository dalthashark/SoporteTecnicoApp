package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityRegistroEquipoBinding
import kotlinx.coroutines.launch

class RegistroEquipoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEquipoBinding
    private lateinit var db: AppDatabase
    private var equipoId: Int = -1
    private var equipoActual: EquipoClienteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEquipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        equipoId = intent.getIntExtra("EQUIPO_ID", -1)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (equipoId != -1) {
            cargarDatosEquipo()
        }

        binding.btnActualizar.setOnClickListener {
            actualizarEquipo()
        }
    }

    private fun cargarDatosEquipo() {
        lifecycleScope.launch {
            db.equipoClienteDao().getById(equipoId)?.let { eq ->
                equipoActual = eq
                binding.etModelo.setText(eq.Modelo)
                binding.etSerie.setText(eq.Num_serie)
                binding.etProcesador.setText(eq.Procesador)
                binding.etRAM.setText(eq.RAM)
                binding.etBateria.setText(eq.Estado_Bateria)
                binding.etExtra.setText(eq.Características)
            }
        }
    }

    private fun actualizarEquipo() {
        equipoActual?.let { eq ->
            lifecycleScope.launch {
                val updatedEq = eq.copy(
                    Modelo = binding.etModelo.text.toString().trim(),
                    Num_serie = binding.etSerie.text.toString().trim(),
                    Procesador = binding.etProcesador.text.toString().trim(),
                    RAM = binding.etRAM.text.toString().trim(),
                    Estado_Bateria = binding.etBateria.text.toString().trim(),
                    Características = binding.etExtra.text.toString().trim()
                )
                db.equipoClienteDao().update(updatedEq)
                Toast.makeText(this@RegistroEquipoActivity, "Ficha técnica actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.CompraEquipoUsadoEntity
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.EstadoComponentesDesarmeEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityRegistroLaboratorioBinding
import kotlinx.coroutines.launch

class RegistroLaboratorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroLaboratorioBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroLaboratorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnGuardar.setOnClickListener {
            guardarEnLaboratorio()
        }
    }

    private fun guardarEnLaboratorio() {
        val tipo = binding.etTipoEquipo.text.toString().trim()
        val modelo = binding.etModelo.text.toString().trim()
        val pantalla = binding.etStatusPantalla.text.toString().trim()
        val placa = binding.etStatusPlaca.text.toString().trim()
        val bateria = binding.etStatusBateria.text.toString().trim()
        val monto = binding.etMontoCompra.text.toString().toDoubleOrNull() ?: 0.0

        if (tipo.isNotEmpty() && modelo.isNotEmpty()) {
            lifecycleScope.launch {
                // 1. Crear Equipo
                val idEq = db.equipoClienteDao().insert(
                    EquipoClienteEntity(
                        Id_cl = 1, // ID Genérico para el inventario de la tienda
                        Id_m = 1,
                        Tipo_equipo = tipo,
                        Modelo = modelo,
                        Num_serie = "COMPRA-LAB",
                        Características = "Para Desarme",
                        Estado_propiedad = "Por Desarme"
                    )
                ).toInt()

                // 2. Guardar estado de componentes
                db.estadoComponentesDesarmeDao().insert(
                    EstadoComponentesDesarmeEntity(
                        Id_eq = idEq,
                        La_Pantalla = pantalla,
                        La_Placa_Madre = placa,
                        La_Bateria = bateria
                    )
                )

                // 3. Registrar Compra
                db.compraEquipoUsadoDao().insert(
                    CompraEquipoUsadoEntity(
                        Id_eq = idEq,
                        Id_cl = 1,
                        La_Fecha_c = System.currentTimeMillis(),
                        El_Monto = monto,
                        El_Motivo = "Desarme",
                        La_Observaciones = "Ingreso a Stock"
                    )
                )

                Toast.makeText(this@RegistroLaboratorioActivity, "Equipo ingresado al laboratorio", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Tipo y Modelo son requeridos", Toast.LENGTH_SHORT).show()
        }
    }
}

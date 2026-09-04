package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
    private var ecdIdParaEditar: Int = -1
    private var idEqRelacionado: Int = -1

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

        configurarDropdowns()

        // Verificar si es edición
        ecdIdParaEditar = intent.getIntExtra("ECD_ID", -1)
        if (ecdIdParaEditar != -1) {
            cargarDatosParaEditar()
        }
    }

    private fun configurarDropdowns() {
        val estados = arrayOf("Perfecto Estado", "Dañado", "Ya Utilizado", "No Presente")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, estados)

        val dropdowns = arrayOf(
            binding.acPantalla, binding.acBateria, binding.acTeclado, binding.acTouchpad,
            binding.acCargador, binding.acPlacaMadre, binding.acProcesador, binding.acMemoria,
            binding.acDisco, binding.acCarcasaSup, binding.acCarcasaInf, binding.acBisagras,
            binding.acCamara, binding.acWifi, binding.acPuertoCarga, binding.acVideo,
            binding.acAltavoces, binding.acMicrofono
        )

        for (ac in dropdowns) {
            ac.setAdapter(adapter)
            // Valor por defecto si es nuevo registro
            if (ecdIdParaEditar == -1) {
                ac.setText(estados[0], false)
            }
        }
    }

    private fun cargarDatosParaEditar() {
        lifecycleScope.launch {
            val ecd = db.estadoComponentesDesarmeDao().getById(ecdIdParaEditar)
            ecd?.let { item ->
                idEqRelacionado = item.Id_eq
                
                // Cargar datos del equipo
                val equipo = db.equipoClienteDao().getById(item.Id_eq)
                equipo?.let { eq ->
                    binding.etTipoEquipo.setText(eq.Tipo_equipo)
                    binding.etModelo.setText(eq.Modelo)
                }

                // Cargar todos los componentes en los dropdowns
                binding.acPantalla.setText(item.La_Pantalla, false)
                binding.acBateria.setText(item.La_Bateria, false)
                binding.acTeclado.setText(item.El_Teclado, false)
                binding.acTouchpad.setText(item.El_Touchpad, false)
                binding.acCargador.setText(item.El_Cargador, false)
                binding.acPlacaMadre.setText(item.La_Placa_Madre, false)
                binding.acProcesador.setText(item.El_Procesador, false)
                binding.acMemoria.setText(item.La_Memoria_RAM, false)
                binding.acDisco.setText(item.El_Disco_Duro, false)
                binding.acCarcasaSup.setText(item.La_Carcasa_Superior, false)
                binding.acCarcasaInf.setText(item.La_Carcasa_Inferior, false)
                binding.acBisagras.setText(item.Las_Bisagras, false)
                binding.acCamara.setText(item.La_Camara_Web, false)
                binding.acWifi.setText(item.La_Tarjeta_Wifi, false)
                binding.acPuertoCarga.setText(item.El_Puerto_Carga, false)
                binding.acVideo.setText(item.La_Tarjeta_Video, false)
                binding.acAltavoces.setText(item.Los_Altavoces, false)
                binding.acMicrofono.setText(item.El_Microfono, false)

                binding.btnGuardar.text = "ACTUALIZAR EN LABORATORIO"
                binding.toolbar.title = "Editar Stock Laboratorio"
            }
        }
    }

    private fun guardarEnLaboratorio() {
        val tipo = binding.etTipoEquipo.text.toString().trim()
        val modelo = binding.etModelo.text.toString().trim()
        val monto = binding.etMontoCompra.text.toString().toDoubleOrNull() ?: 0.0

        if (tipo.isNotEmpty() && modelo.isNotEmpty()) {
            lifecycleScope.launch {
                val idEq: Int
                if (ecdIdParaEditar == -1) {
                    // 1. Crear Equipo Nuevo
                    idEq = db.equipoClienteDao().insert(
                        EquipoClienteEntity(
                            Id_cl = 1, // DATALAB (ALMACÉN)
                            Id_m = 1,
                            Tipo_equipo = tipo,
                            Modelo = modelo,
                            Num_serie = "COMPRA-LAB",
                            Características = "Para Desarme",
                            Estado_propiedad = "Por Desarme"
                        )
                    ).toInt()

                    // 2. Registrar Compra
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
                } else {
                    idEq = idEqRelacionado
                    // Actualizar equipo
                    val eqExistente = db.equipoClienteDao().getById(idEq)
                    eqExistente?.let {
                        db.equipoClienteDao().update(it.copy(Tipo_equipo = tipo, Modelo = modelo))
                    }
                }

                // 3. Guardar/Actualizar estado de componentes
                val ecd = EstadoComponentesDesarmeEntity(
                    Id_ecd = if (ecdIdParaEditar != -1) ecdIdParaEditar else 0,
                    Id_eq = idEq,
                    La_Pantalla = binding.acPantalla.text.toString(),
                    La_Bateria = binding.acBateria.text.toString(),
                    El_Teclado = binding.acTeclado.text.toString(),
                    El_Touchpad = binding.acTouchpad.text.toString(),
                    El_Cargador = binding.acCargador.text.toString(),
                    La_Placa_Madre = binding.acPlacaMadre.text.toString(),
                    El_Procesador = binding.acProcesador.text.toString(),
                    La_Memoria_RAM = binding.acMemoria.text.toString(),
                    El_Disco_Duro = binding.acDisco.text.toString(),
                    La_Tarjeta_Video = binding.acVideo.text.toString(),
                    La_Carcasa_Superior = binding.acCarcasaSup.text.toString(),
                    La_Carcasa_Inferior = binding.acCarcasaInf.text.toString(),
                    Las_Bisagras = binding.acBisagras.text.toString(),
                    La_Camara_Web = binding.acCamara.text.toString(),
                    El_Microfono = binding.acMicrofono.text.toString(),
                    Los_Altavoces = binding.acAltavoces.text.toString(),
                    La_Tarjeta_Wifi = binding.acWifi.text.toString(),
                    El_Puerto_Carga = binding.acPuertoCarga.text.toString()
                )

                if (ecdIdParaEditar == -1) {
                    db.estadoComponentesDesarmeDao().insert(ecd)
                    Toast.makeText(this@RegistroLaboratorioActivity, "Equipo ingresado al laboratorio", Toast.LENGTH_SHORT).show()
                } else {
                    db.estadoComponentesDesarmeDao().update(ecd)
                    Toast.makeText(this@RegistroLaboratorioActivity, "Stock actualizado", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        } else {
            Toast.makeText(this, "Tipo y Modelo son requeridos", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.OrdenServicioEntity
import com.mantenimiento.soportetecnicoapp.data.entity.RepuestoOrdenEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityGestionMantenimientoBinding
import kotlinx.coroutines.launch

class GestionMantenimientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionMantenimientoBinding
    private lateinit var db: AppDatabase
    private var ordenId: Int = -1
    private var ordenActual: OrdenServicioEntity? = null
    private lateinit var repuestoAdapter: RepuestoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionMantenimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar Combo Box de Estados
        val estados = arrayOf("Recibido", "En Proceso", "Esperando Repuesto", "Listo para Entrega", "Entregado")
        val adapterEstados = ArrayAdapter(this, android.R.layout.simple_list_item_1, estados)
        binding.acEstado.setAdapter(adapterEstados)

        // Configurar Lista de Repuestos
        repuestoAdapter = RepuestoAdapter(emptyList())
        binding.rvRepuestos.layoutManager = LinearLayoutManager(this)
        binding.rvRepuestos.adapter = repuestoAdapter

        if (ordenId != -1) {
            cargarDatosOrden()
        }

        binding.btnAgregarRepuesto.setOnClickListener { mostrarDialogoRepuesto() }
        binding.btnActualizarOrden.setOnClickListener { actualizarOrden() }
    }

    private fun cargarDatosOrden() {
        lifecycleScope.launch {
            db.ordenServicioDao().getById(ordenId)?.let { orden ->
                ordenActual = orden
                binding.tvOrdenTitulo.text = "Orden #$ordenId"
                binding.tvFallaDetalle.text = "Falla: ${orden.Falla_reportada}"
                binding.etDiagnostico.setText(orden.Diagnostico)
                binding.acEstado.setText(orden.Estado, false)
                
                // Cargar datos del dueño
                val equipo = db.equipoClienteDao().getById(orden.Id_eq)
                val cliente = equipo?.let { db.clienteDao().getById(it.Id_cl) }
                cliente?.let {
                    binding.tvClienteGestion.text = "Dueño: ${it.Nombre_cl} (${it.Telefono})"
                }
            }

            db.repuestoOrdenDao().getByOrden(ordenId).collect { lista ->
                repuestoAdapter.updateData(lista)
            }
        }
    }

    private fun mostrarDialogoRepuesto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir Repuesto")
        
        val view = layoutInflater.inflate(android.R.layout.select_dialog_item, null) // Placeholder simple
        // Para mayor rapidez creamos un layout de diálogo programático o simple
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etNombre = EditText(this).apply { hint = "Nombre del Repuesto (ej: Batería HP)" }
        val etPrecio = EditText(this).apply { 
            hint = "Precio S/."
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etCant = EditText(this).apply { 
            hint = "Cantidad"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText("1")
        }

        layout.addView(etNombre)
        layout.addView(etCant)
        layout.addView(etPrecio)
        builder.setView(layout)

        builder.setPositiveButton("Añadir") { _, _ ->
            val nombre = etNombre.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val cant = etCant.text.toString().toIntOrNull() ?: 1

            if (nombre.isNotEmpty()) {
                lifecycleScope.launch {
                    db.repuestoOrdenDao().insert(
                        RepuestoOrdenEntity(
                            Id_os = ordenId,
                            Nombre_repuesto = nombre,
                            Cantidad = cant,
                            Precio_unitario = precio
                        )
                    )
                    Toast.makeText(this@GestionMantenimientoActivity, "Repuesto añadido", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun actualizarOrden() {
        val nuevoEstado = binding.acEstado.text.toString()
        val nuevoDiagnostico = binding.etDiagnostico.text.toString()

        ordenActual?.let { orden ->
            lifecycleScope.launch {
                db.ordenServicioDao().update(
                    orden.copy(
                        Estado = nuevoEstado,
                        Diagnostico = nuevoDiagnostico
                    )
                )
                Toast.makeText(this@GestionMantenimientoActivity, "Avances guardados correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

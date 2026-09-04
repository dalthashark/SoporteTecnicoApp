package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.data.entity.DetalleVentaEntity
import com.mantenimiento.soportetecnicoapp.data.entity.VentaEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityRegistroVentaBinding
import kotlinx.coroutines.launch

class RegistroVentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroVentaBinding
    private lateinit var db: AppDatabase
    private var listaClientes: List<ClienteEntity> = emptyList()
    private var clienteSeleccionado: ClienteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNuevoClienteRapido.setOnClickListener {
            startActivity(Intent(this, RegistroClienteActivity::class.java))
        }

        configurarSelectores()

        binding.btnGuardarVenta.setOnClickListener {
            procesarVenta()
        }
    }

    private fun configurarSelectores() {
        // Selector de Clientes
        lifecycleScope.launch {
            db.clienteDao().getAll().collect { clientes ->
                // FILTRO: No mostrar el almacén en el selector de clientes
                listaClientes = clientes.filter { it.Nombre_cl != "DATALAB (ALMACÉN)" }
                val nombres = listaClientes.map { it.Nombre_cl }
                val adapter = ArrayAdapter(this@RegistroVentaActivity, android.R.layout.simple_list_item_1, nombres)
                binding.acCliente.setAdapter(adapter)
            }
        }

        binding.acCliente.setOnItemClickListener { _, _, position, _ ->
            val nombreSeleccionado = binding.acCliente.adapter.getItem(position).toString()
            clienteSeleccionado = listaClientes.find { it.Nombre_cl == nombreSeleccionado }
        }

        // Métodos de Pago
        val metodos = arrayOf("Efectivo", "Yape / Plin", "Transferencia", "Tarjeta")
        val adapterMetodo = ArrayAdapter(this, android.R.layout.simple_list_item_1, metodos)
        binding.acMetodoPago.setAdapter(adapterMetodo)
        binding.acMetodoPago.setText(metodos[0], false)
    }

    private fun procesarVenta() {
        val desc = binding.etDescripcion.text.toString().trim()
        val monto = binding.etMonto.text.toString().toDoubleOrNull() ?: 0.0
        val metodo = binding.acMetodoPago.text.toString()

        if (clienteSeleccionado != null && desc.isNotEmpty() && monto > 0) {
            lifecycleScope.launch {
                try {
                    // 1. Registrar Cabecera de Venta
                    val idVenta = db.ventaDao().insert(
                        VentaEntity(
                            Id_cl = clienteSeleccionado!!.Id_cl,
                            Id_os = null,
                            Fecha_venta = System.currentTimeMillis(),
                            Tipo_comprobante = "Boleta / Recibo",
                            Metodo_pago = metodo,
                            Monto_total = monto
                        )
                    ).toInt()

                    // 2. Registrar Detalle
                    db.detalleVentaDao().insert(
                        DetalleVentaEntity(
                            Id_v = idVenta,
                            Id_p = null,
                            Descripcion_servicio = desc,
                            Cantidad = 1,
                            Precio_unitario = monto,
                            Subtotal = monto
                        )
                    )

                    Toast.makeText(this@RegistroVentaActivity, "Venta procesada con éxito", Toast.LENGTH_SHORT).show()
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this@RegistroVentaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Seleccione cliente y complete descripción/monto", Toast.LENGTH_SHORT).show()
        }
    }
}

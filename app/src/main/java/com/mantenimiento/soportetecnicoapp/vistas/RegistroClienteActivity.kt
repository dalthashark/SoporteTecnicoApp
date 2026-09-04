package com.mantenimiento.soportetecnicoapp.vistas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityRegistroClienteBinding
import kotlinx.coroutines.launch

class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var db: AppDatabase
    private var clienteIdParaEditar: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val tiposDoc = arrayOf("DNI", "RUC", "PASAPORTE", "CE")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tiposDoc)
        binding.acTipoDoc.setAdapter(adapter)
        binding.acTipoDoc.setText(tiposDoc[0], false)

        binding.btnGuardar.setOnClickListener {
            guardarCliente()
        }

        // Verificar si es edición
        clienteIdParaEditar = intent.getIntExtra("CLIENTE_ID", -1)
        if (clienteIdParaEditar != -1) {
            cargarDatosParaEditar()
        }
    }

    private fun cargarDatosParaEditar() {
        lifecycleScope.launch {
            val cliente = db.clienteDao().getById(clienteIdParaEditar)
            cliente?.let { c ->
                binding.etNombre.setText(c.Nombre_cl)
                binding.etNumDoc.setText(if (c.Num_doc == "SIN DOCUMENTO") "" else c.Num_doc)
                binding.acTipoDoc.setText(c.Tipo_doc, false)
                binding.etTelefono.setText(c.Telefono)
                binding.etCorreo.setText(c.Correo)
                
                binding.btnGuardar.text = "ACTUALIZAR DATOS"
                binding.toolbar.title = "Editar Cliente"
            }
        }
    }

    private fun guardarCliente() {
        val nombre = binding.etNombre.text.toString().trim()
        val numDoc = binding.etNumDoc.text.toString().trim()
        val tipoDoc = binding.acTipoDoc.text.toString()
        val telefono = binding.etTelefono.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación de Teléfono (9 dígitos)
        if (telefono.isEmpty() || telefono.length != 9) {
            binding.etTelefono.error = "El teléfono debe tener 9 dígitos"
            Toast.makeText(this, "El teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación de Documento según tipo
        if (numDoc.isNotEmpty()) {
            if (tipoDoc == "DNI" && numDoc.length != 8) {
                binding.etNumDoc.error = "El DNI debe tener 8 dígitos"
                Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show()
                return
            }
            if (tipoDoc == "RUC" && numDoc.length != 11) {
                binding.etNumDoc.error = "El RUC debe tener 11 dígitos"
                Toast.makeText(this, "El RUC debe tener 11 dígitos", Toast.LENGTH_SHORT).show()
                return
            }
        }

        lifecycleScope.launch {
            val cliente = ClienteEntity(
                Id_cl = if (clienteIdParaEditar != -1) clienteIdParaEditar else 0,
                Nombre_cl = nombre,
                Tipo_doc = if (numDoc.isEmpty()) "S/D" else tipoDoc,
                Num_doc = if (numDoc.isEmpty()) "SIN DOCUMENTO" else numDoc,
                Telefono = telefono,
                Correo = correo
            )

            if (clienteIdParaEditar == -1) {
                db.clienteDao().insert(cliente)
                Toast.makeText(this@RegistroClienteActivity, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()
            } else {
                db.clienteDao().update(cliente)
                Toast.makeText(this@RegistroClienteActivity, "Cliente actualizado con éxito", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}

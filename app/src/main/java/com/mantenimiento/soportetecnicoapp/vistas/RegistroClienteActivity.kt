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
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tiposDoc)
        binding.spnTipoDoc.adapter = spinnerAdapter

        binding.btnGuardar.setOnClickListener {
            guardarCliente()
        }
    }

    private fun guardarCliente() {
        val nombre = binding.etNombre.text.toString().trim()
        val numDoc = binding.etNumDoc.text.toString().trim()
        val tipoDoc = binding.spnTipoDoc.selectedItem.toString()
        val telefono = binding.etTelefono.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()

        if (nombre.isNotEmpty() && telefono.isNotEmpty()) {
            lifecycleScope.launch {
                db.clienteDao().insert(
                    ClienteEntity(
                        Nombre_cl = nombre,
                        Tipo_doc = if (numDoc.isEmpty()) "S/D" else tipoDoc,
                        Num_doc = if (numDoc.isEmpty()) "SIN DOCUMENTO" else numDoc,
                        Telefono = telefono,
                        Correo = correo
                    )
                )
                Toast.makeText(this@RegistroClienteActivity, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Nombre y Teléfono son obligatorios", Toast.LENGTH_SHORT).show()
        }
    }
}

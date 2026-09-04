package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.databinding.ActivityTallerBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TallerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTallerBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: OrdenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTallerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.fabVolver.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = OrdenAdapter(emptyList()) { orden ->
            val intent = Intent(this, GestionMantenimientoActivity::class.java)
            intent.putExtra("ORDEN_ID", orden.Id_os)
            startActivity(intent)
        }
        
        binding.rvOrdenesTaller.layoutManager = LinearLayoutManager(this)
        binding.rvOrdenesTaller.adapter = adapter

        cargarOrdenesActivas()
    }

    private fun cargarOrdenesActivas() {
        lifecycleScope.launch {
            combine(
                db.ordenServicioDao().getAll(),
                db.equipoClienteDao().getAll(),
                db.clienteDao().getAll()
            ) { listaOrdenes, listaEquipos, listaClientes ->
                listaOrdenes
                    .filter { it.Estado != "Entregado" && it.Estado != "Cerrado" }
                    .map { orden ->
                        val equipo = listaEquipos.find { it.Id_eq == orden.Id_eq }
                        val cliente = listaClientes.find { it.Id_cl == equipo?.Id_cl }
                        OrdenAdapter.OrdenConCliente(orden, cliente)
                    }
            }.collect { data ->
                adapter.updateData(data)
            }
        }
    }
}

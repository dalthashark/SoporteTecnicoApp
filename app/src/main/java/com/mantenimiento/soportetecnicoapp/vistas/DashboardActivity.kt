package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mantenimiento.soportetecnicoapp.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar navegación
        binding.cardVenta.setOnClickListener {
            startActivity(Intent(this, RegistroVentaActivity::class.java))
        }

        binding.cardNuevaOrden.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.cardClientes.setOnClickListener {
            startActivity(Intent(this, ListaClientesActivity::class.java))
        }

        binding.cardRegCliente.setOnClickListener {
            startActivity(Intent(this, RegistroClienteActivity::class.java))
        }

        binding.cardLaboratorio.setOnClickListener {
            startActivity(Intent(this, LaboratorioActivity::class.java))
        }

        binding.cardRegLaptop.setOnClickListener {
            startActivity(Intent(this, RegistroLaboratorioActivity::class.java))
        }

        binding.cardRegTecnico.setOnClickListener {
            startActivity(Intent(this, RegistroTecnicoActivity::class.java))
        }

        binding.cardTecnicos.setOnClickListener {
            startActivity(Intent(this, TecnicosActivity::class.java))
        }

        binding.cardAsistencia.setOnClickListener {
            startActivity(Intent(this, AsistenciaActivity::class.java))
        }

        binding.cardTaller.setOnClickListener {
            startActivity(Intent(this, TallerActivity::class.java))
        }
    }
}

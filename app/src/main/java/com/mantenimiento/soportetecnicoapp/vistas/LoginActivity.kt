package com.mantenimiento.soportetecnicoapp.vistas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mantenimiento.soportetecnicoapp.databinding.ActivityLoginBinding
import com.mantenimiento.soportetecnicoapp.data.AppDatabase
import com.mantenimiento.soportetecnicoapp.data.entity.UsuarioEntity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Crear datos iniciales si la base de datos está vacía
        lifecycleScope.launch {
            if (db.usuarioDao().getCount() == 0) {
                // Datos para que no fallen las FK en el ejemplo rápido
                db.categoriaDao().insert(com.mantenimiento.soportetecnicoapp.data.entity.CategoriaEntity(Nombre_c = "General"))
                db.marcaDao().insert(com.mantenimiento.soportetecnicoapp.data.entity.MarcaEntity(Nombre_m = "Genérica"))
                
                // Usuario Admin Profesional
                db.usuarioDao().insert(UsuarioEntity(
                    username = "admin", 
                    password = "admin", 
                    nombre_completo = "Administrador Datalab",
                    rol = "Dueño"
                ))
                
                // Ejemplo para el Laboratorio
                val idEq = db.equipoClienteDao().insert(com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity(
                    Id_cl = 1, Id_m = 1, Tipo_equipo = "Laptop", Modelo = "Prueba Lab", 
                    Num_serie = "SN-LAB-001", Características = "Desarme", Estado_propiedad = "Por Desarme"
                )).toInt()
                
                db.estadoComponentesDesarmeDao().insert(com.mantenimiento.soportetecnicoapp.data.entity.EstadoComponentesDesarmeEntity(
                    Id_eq = idEq, La_Pantalla = "OK", La_Placa_Madre = "Falla Eléctrica", La_Bateria = "Agotada"
                ))
            }
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    val usuario = db.usuarioDao().login(user, pass)
                    if (usuario != null) {
                        Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre_completo}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

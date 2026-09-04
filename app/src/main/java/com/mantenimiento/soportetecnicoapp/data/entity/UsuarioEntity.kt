package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id_u: Int = 0,
    val username: String,
    val password: String,
    val nombre_completo: String,
    val rol: String = "Técnico" // Dueño, Gerente, Técnico
)

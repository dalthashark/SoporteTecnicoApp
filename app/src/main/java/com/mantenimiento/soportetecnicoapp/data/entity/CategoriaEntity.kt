package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_c: Int = 0,
    val Nombre_c: String
)

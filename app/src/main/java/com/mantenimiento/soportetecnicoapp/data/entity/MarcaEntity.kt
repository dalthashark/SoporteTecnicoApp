package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marca")
data class MarcaEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_m: Int = 0,
    val Nombre_m: String
)

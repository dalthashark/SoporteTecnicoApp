package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cliente")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_cl: Int = 0,
    val Nombre_cl: String,
    val Tipo_doc: String,
    val Num_doc: String,
    val Telefono: String,
    val Correo: String
)

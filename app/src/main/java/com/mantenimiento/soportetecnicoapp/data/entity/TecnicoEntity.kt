package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tecnico")
data class TecnicoEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_t: Int = 0,
    val Nombre_t: String,
    val Telefono: String,
    val Especialidad: String,
    val Instituto: String? = null,
    val Carrera: String? = null,
    val Tipo_trabajador: String = "Trabajador", // Practicante o Trabajador
    val Foto_path: String? = null,
    val Dias_trabajo: String? = null, // Ej: "Lun, Mié, Vie"
    val Hora_entrada: String? = null,
    val Hora_salida: String? = null
)

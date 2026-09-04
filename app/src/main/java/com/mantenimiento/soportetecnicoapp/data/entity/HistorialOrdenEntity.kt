package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historial_orden",
    foreignKeys = [
        ForeignKey(
            entity = OrdenServicioEntity::class,
            parentColumns = ["Id_os"],
            childColumns = ["Id_os"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Id_os")]
)
data class HistorialOrdenEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_ho: Int = 0,
    val Id_os: Int,
    val Fecha_cambio: Long,
    val Estado_anterior: String,
    val Estado_nuevo: String,
    val Notas_tecnico: String
)

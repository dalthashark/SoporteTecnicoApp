package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orden_servicio",
    foreignKeys = [
        ForeignKey(
            entity = EquipoClienteEntity::class,
            parentColumns = ["Id_eq"],
            childColumns = ["Id_eq"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TecnicoEntity::class,
            parentColumns = ["Id_t"],
            childColumns = ["Id_t"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("Id_eq"), Index("Id_t")]
)
data class OrdenServicioEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_os: Int = 0,
    val Id_eq: Int,
    val Id_t: Int? = null,
    val Fecha_ingreso: Long,
    val Falla_reportada: String,
    val Diagnostico: String,
    val Estado: String,
    val Costo_mano_obra: Double,
    val Fecha_entrega: Long? = null,
    val Foto_path: String? = null
)

package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "compra_equipo_usado",
    foreignKeys = [
        ForeignKey(
            entity = EquipoClienteEntity::class,
            parentColumns = ["Id_eq"],
            childColumns = ["Id_eq"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["Id_cl"],
            childColumns = ["Id_cl"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("Id_eq"), Index("Id_cl")]
)
data class CompraEquipoUsadoEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_ceu: Int = 0,
    val Id_eq: Int,
    val Id_cl: Int?,
    val La_Fecha_c: Long,
    val El_Monto: Double,
    val El_Motivo: String,
    val La_Observaciones: String
)

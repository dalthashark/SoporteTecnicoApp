package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalle_venta",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["Id_v"],
            childColumns = ["Id_v"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["Id_p"],
            childColumns = ["Id_p"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("Id_v"), Index("Id_p")]
)
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_dv: Int = 0,
    val Id_v: Int,
    val Id_p: Int? = null,
    val Descripcion_servicio: String? = null,
    val Cantidad: Int,
    val Precio_unitario: Double,
    val Subtotal: Double
)

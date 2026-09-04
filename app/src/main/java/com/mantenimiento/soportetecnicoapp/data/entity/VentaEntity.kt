package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "venta",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["Id_cl"],
            childColumns = ["Id_cl"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = OrdenServicioEntity::class,
            parentColumns = ["Id_os"],
            childColumns = ["Id_os"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("Id_cl"), Index("Id_os")]
)
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_v: Int = 0,
    val Id_cl: Int? = null,
    val Id_os: Int? = null,
    val Fecha_venta: Long,
    val Tipo_comprobante: String,
    val Metodo_pago: String,
    val Monto_total: Double
)

package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repuesto_orden",
    foreignKeys = [
        ForeignKey(
            entity = OrdenServicioEntity::class,
            parentColumns = ["Id_os"],
            childColumns = ["Id_os"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["Id_p"],
            childColumns = ["Id_p"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Id_os"), Index("Id_p")]
)
data class RepuestoOrdenEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_ro: Int = 0,
    val Id_os: Int,
    val Id_p: Int? = null, // Opcional si es un repuesto no inventariado
    val Nombre_repuesto: String, // "Batería HP", "Ventilador", etc.
    val Cantidad: Int,
    val Precio_unitario: Double
)

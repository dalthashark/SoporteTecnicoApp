package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "producto",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["Id_c"],
            childColumns = ["Id_c"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = MarcaEntity::class,
            parentColumns = ["Id_m"],
            childColumns = ["Id_m"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("Codigo_sku", unique = true),
        Index("Id_c"),
        Index("Id_m")
    ]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_p: Int = 0,
    val Codigo_sku: String,
    val Nombre_p: String,
    val Id_c: Int?,
    val Id_m: Int?,
    val Precio_costo: Double,
    val Precio_venta: Double,
    val Stock_actual: Int,
    val Stock_minimo: Int,
    val Ubicacion: String
)

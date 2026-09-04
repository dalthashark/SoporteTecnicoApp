package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipo_cliente",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["Id_cl"],
            childColumns = ["Id_cl"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MarcaEntity::class,
            parentColumns = ["Id_m"],
            childColumns = ["Id_m"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("Id_cl"), Index("Id_m")]
)
data class EquipoClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_eq: Int = 0,
    val Id_cl: Int,
    val Id_m: Int?,
    val Tipo_equipo: String,
    val Modelo: String,
    val Num_serie: String,
    val Características: String,
    val Estado_propiedad: String
)

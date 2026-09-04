package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estado_componentes_desarme",
    foreignKeys = [
        ForeignKey(
            entity = EquipoClienteEntity::class,
            parentColumns = ["Id_eq"],
            childColumns = ["Id_eq"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("Id_eq")]
)
data class EstadoComponentesDesarmeEntity(
    @PrimaryKey(autoGenerate = true)
    val Id_ecd: Int = 0,
    val Id_eq: Int,
    val La_Pantalla: String? = null,
    val La_Bateria: String? = null,
    val El_Teclado: String? = null,
    val El_Touchpad: String? = null,
    val El_Cargador: String? = null,
    val La_Placa_Madre: String? = null,
    val El_Procesador: String? = null,
    val La_Memoria_RAM: String? = null,
    val El_Disco_Duro: String? = null,
    val La_Tarjeta_Video: String? = null,
    val La_Carcasa_Superior: String? = null,
    val La_Carcasa_Inferior: String? = null,
    val Las_Bisagras: String? = null,
    val La_Camara_Web: String? = null,
    val El_Microfono: String? = null,
    val Los_Altavoces: String? = null,
    val La_Tarjeta_Wifi: String? = null,
    val El_Puerto_Carga: String? = null
)

package com.mantenimiento.soportetecnicoapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asistencia",
    foreignKeys = [
        ForeignKey(
            entity = TecnicoEntity::class,
            parentColumns = ["Id_t"],
            childColumns = ["id_tecnico"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_tecnico"), Index("id_tecnico", "fecha", unique = true)]
)
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id_asistencia: Int = 0,
    val id_tecnico: Int,
    val fecha: Long, // Timestamp del día (solo fecha)
    val estado: String, // 'ASISTIÓ', 'FALTA', 'JUSTIFICADO'
    val observacion: String? = null,
    val foto_justificacion_path: String? = null
)

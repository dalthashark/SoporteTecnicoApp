package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.AsistenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao : BaseDao<AsistenciaEntity> {
    @Query("SELECT * FROM asistencia WHERE fecha = :fecha")
    fun getAsistenciaPorFecha(fecha: Long): Flow<List<AsistenciaEntity>>

    @Query("SELECT * FROM asistencia WHERE id_tecnico = :idTecnico")
    fun getHistorialPorTecnico(idTecnico: Int): Flow<List<AsistenciaEntity>>
}

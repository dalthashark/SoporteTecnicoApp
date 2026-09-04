package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.EstadoComponentesDesarmeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoComponentesDesarmeDao : BaseDao<EstadoComponentesDesarmeEntity> {
    @Query("SELECT * FROM estado_componentes_desarme")
    fun getAll(): Flow<List<EstadoComponentesDesarmeEntity>>

    @Query("SELECT * FROM estado_componentes_desarme WHERE Id_ecd = :id")
    suspend fun getById(id: Int): EstadoComponentesDesarmeEntity?
}

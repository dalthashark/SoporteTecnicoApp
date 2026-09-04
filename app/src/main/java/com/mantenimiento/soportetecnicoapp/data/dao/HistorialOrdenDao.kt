package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.HistorialOrdenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialOrdenDao : BaseDao<HistorialOrdenEntity> {
    @Query("SELECT * FROM historial_orden")
    fun getAll(): Flow<List<HistorialOrdenEntity>>

    @Query("SELECT * FROM historial_orden WHERE Id_ho = :id")
    suspend fun getById(id: Int): HistorialOrdenEntity?
}

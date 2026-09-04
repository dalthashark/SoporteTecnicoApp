package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.VentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao : BaseDao<VentaEntity> {
    @Query("SELECT * FROM venta")
    fun getAll(): Flow<List<VentaEntity>>

    @Query("SELECT * FROM venta WHERE Id_v = :id")
    suspend fun getById(id: Int): VentaEntity?
}

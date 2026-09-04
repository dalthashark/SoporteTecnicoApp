package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.DetalleVentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao : BaseDao<DetalleVentaEntity> {
    @Query("SELECT * FROM detalle_venta")
    fun getAll(): Flow<List<DetalleVentaEntity>>

    @Query("SELECT * FROM detalle_venta WHERE Id_dv = :id")
    suspend fun getById(id: Int): DetalleVentaEntity?
}

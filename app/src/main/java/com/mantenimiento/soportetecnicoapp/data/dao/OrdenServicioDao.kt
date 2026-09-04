package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.OrdenServicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenServicioDao : BaseDao<OrdenServicioEntity> {
    @Query("SELECT * FROM orden_servicio")
    fun getAll(): Flow<List<OrdenServicioEntity>>

    @Query("SELECT * FROM orden_servicio WHERE Id_os = :id")
    suspend fun getById(id: Int): OrdenServicioEntity?
}

package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.RepuestoOrdenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepuestoOrdenDao : BaseDao<RepuestoOrdenEntity> {
    @Query("SELECT * FROM repuesto_orden")
    fun getAll(): Flow<List<RepuestoOrdenEntity>>

    @Query("SELECT * FROM repuesto_orden WHERE Id_ro = :id")
    suspend fun getById(id: Int): RepuestoOrdenEntity?

    @Query("SELECT * FROM repuesto_orden WHERE Id_os = :idOs")
    fun getByOrden(idOs: Int): Flow<List<RepuestoOrdenEntity>>
}

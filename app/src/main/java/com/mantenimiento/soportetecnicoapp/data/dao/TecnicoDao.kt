package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.TecnicoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TecnicoDao : BaseDao<TecnicoEntity> {
    @Query("SELECT * FROM tecnico")
    fun getAll(): Flow<List<TecnicoEntity>>

    @Query("SELECT * FROM tecnico WHERE Id_t = :id")
    suspend fun getById(id: Int): TecnicoEntity?
}

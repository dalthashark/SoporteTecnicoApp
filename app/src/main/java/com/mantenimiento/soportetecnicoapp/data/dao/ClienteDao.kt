package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao : BaseDao<ClienteEntity> {
    @Query("SELECT * FROM cliente")
    fun getAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM cliente WHERE Id_cl = :id")
    suspend fun getById(id: Int): ClienteEntity?
}

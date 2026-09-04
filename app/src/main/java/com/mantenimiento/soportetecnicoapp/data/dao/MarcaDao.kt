package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.MarcaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcaDao : BaseDao<MarcaEntity> {
    @Query("SELECT * FROM marca")
    fun getAll(): Flow<List<MarcaEntity>>

    @Query("SELECT * FROM marca WHERE Id_m = :id")
    suspend fun getById(id: Int): MarcaEntity?
}

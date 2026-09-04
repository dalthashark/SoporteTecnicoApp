package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao : BaseDao<CategoriaEntity> {
    @Query("SELECT * FROM categoria")
    fun getAll(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categoria WHERE Id_c = :id")
    suspend fun getById(id: Int): CategoriaEntity?
}

package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao : BaseDao<ProductoEntity> {
    @Query("SELECT * FROM producto")
    fun getAll(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto WHERE Id_p = :id")
    suspend fun getById(id: Int): ProductoEntity?
}

package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.EquipoClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoClienteDao : BaseDao<EquipoClienteEntity> {
    @Query("SELECT * FROM equipo_cliente")
    fun getAll(): Flow<List<EquipoClienteEntity>>

    @Query("SELECT * FROM equipo_cliente WHERE Id_eq = :id")
    suspend fun getById(id: Int): EquipoClienteEntity?
}

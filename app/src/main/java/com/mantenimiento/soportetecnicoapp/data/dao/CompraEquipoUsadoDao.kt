package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.CompraEquipoUsadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraEquipoUsadoDao : BaseDao<CompraEquipoUsadoEntity> {
    @Query("SELECT * FROM compra_equipo_usado")
    fun getAll(): Flow<List<CompraEquipoUsadoEntity>>

    @Query("SELECT * FROM compra_equipo_usado WHERE Id_ceu = :id")
    suspend fun getById(id: Int): CompraEquipoUsadoEntity?
}

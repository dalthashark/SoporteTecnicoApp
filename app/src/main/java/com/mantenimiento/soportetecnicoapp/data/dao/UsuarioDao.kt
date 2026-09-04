package com.mantenimiento.soportetecnicoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mantenimiento.soportetecnicoapp.data.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE username = :user AND password = :pass LIMIT 1")
    suspend fun login(user: String, pass: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int
}

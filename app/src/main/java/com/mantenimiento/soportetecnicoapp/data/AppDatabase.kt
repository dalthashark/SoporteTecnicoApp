package com.mantenimiento.soportetecnicoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mantenimiento.soportetecnicoapp.data.dao.*
import com.mantenimiento.soportetecnicoapp.data.entity.*

@Database(
    entities = [
        CategoriaEntity::class,
        MarcaEntity::class,
        TecnicoEntity::class,
        ClienteEntity::class,
        EquipoClienteEntity::class,
        ProductoEntity::class,
        OrdenServicioEntity::class,
        RepuestoOrdenEntity::class,
        HistorialOrdenEntity::class,
        VentaEntity::class,
        DetalleVentaEntity::class,
        CompraEquipoUsadoEntity::class,
        EstadoComponentesDesarmeEntity::class,
        UsuarioEntity::class,
        AsistenciaEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun marcaDao(): MarcaDao
    abstract fun tecnicoDao(): TecnicoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun equipoClienteDao(): EquipoClienteDao
    abstract fun productoDao(): ProductoDao
    abstract fun ordenServicioDao(): OrdenServicioDao
    abstract fun repuestoOrdenDao(): RepuestoOrdenDao
    abstract fun historialOrdenDao(): HistorialOrdenDao
    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao
    abstract fun compraEquipoUsadoDao(): CompraEquipoUsadoDao
    abstract fun estadoComponentesDesarmeDao(): EstadoComponentesDesarmeDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun asistenciaDao(): AsistenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "soporte_tecnico_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

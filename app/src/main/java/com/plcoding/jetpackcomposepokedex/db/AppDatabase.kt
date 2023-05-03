package com.plcoding.jetpackcomposepokedex.db

import androidx.room.migration.Migration
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.plcoding.jetpackcomposepokedex.data.models.PokeBall
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Pokemon::class, PokeBall::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokeBallDao(): PokeBallDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("AppDatabase", "onCreate() called")

                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    val pokeBallDao = INSTANCE!!.pokeBallDao()
                    val pokeBall = PokeBall(id = 1,count = 5)
                    pokeBallDao.insert(pokeBall)
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            val migration_1_3 = object : Migration(1, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Perform any necessary schema changes here
                    database.execSQL("CREATE TABLE IF NOT EXISTS `pokeballs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `count` INTEGER NOT NULL)")
                    database.execSQL("INSERT INTO `pokeballs` (`count`) VALUES (5)")
                    database.execSQL("DELETE FROM `pokemon`")
                }
            }
            Log.d("AppDatabase", "getDatabase() called")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokedex.db"
                )

                    .addMigrations(migration_1_3)
                    .addCallback(callback)
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }


}
package com.plcoding.jetpackcomposepokedex.db

import androidx.room.*
import com.plcoding.jetpackcomposepokedex.data.models.PokeBall
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon

@Dao
interface PokeBallDao {

    @Insert
    suspend fun insert(pokeBall: PokeBall)

    @Update
    suspend fun update(pokeBall: PokeBall)

    @Query("SELECT COALESCE(SUM(count), 0) FROM pokeballs")
    suspend fun getTotalCount(): Int

    @Delete
    suspend fun delete(pokeBall: PokeBall)

    @Query("SELECT * FROM pokeballs WHERE id = :id")
    suspend fun getById(id: Int): PokeBall?

}
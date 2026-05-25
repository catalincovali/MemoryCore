package com.catalincovali.memorycore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insert(game: GameEntity)
    @Query("SELECT * FROM games ORDER BY id DESC")
    fun observeAll(): Flow<List<GameEntity>>
}

package com.catalincovali.memorycore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val maxCorrectLength : Int,
    val sequence: String,
    val errorIndex: Int
)


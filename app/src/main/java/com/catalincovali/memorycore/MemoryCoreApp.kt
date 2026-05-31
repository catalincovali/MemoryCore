package com.catalincovali.memorycore

import android.app.Application
import androidx.room.Room
import com.catalincovali.memorycore.data.AppDatabase
import com.catalincovali.memorycore.data.GameRepository

class MemoryCoreApp : Application() {

    // apriamo il DB solo al prima accesso
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "memorycore.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    val gameRepository: GameRepository by lazy {
        GameRepository(database.gameDao())
    }
}

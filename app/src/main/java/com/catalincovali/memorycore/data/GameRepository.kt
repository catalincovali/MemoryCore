package com.catalincovali.memorycore.data

import com.catalincovali.memorycore.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Bridge entity - domain
class GameRepository(private val dao: GameDao) {

    val games: Flow<List<Game>> = dao.observeAll()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun insert(game: Game) {
        dao.insert(game.toEntity())
    }
}

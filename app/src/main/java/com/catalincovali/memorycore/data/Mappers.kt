package com.catalincovali.memorycore.data

import com.catalincovali.memorycore.Game

fun GameEntity.toDomain(): Game = Game(
    id = id,
    maxCorrectLength = maxCorrectLength,
    errorSequence = if (sequence.isEmpty()) emptyList() else sequence.split(","),
    errorIndex = errorIndex
)

fun Game.toEntity(): GameEntity = GameEntity(
    id = id,
    maxCorrectLength = maxCorrectLength,
    sequence = errorSequence.joinToString(","),
    errorIndex = errorIndex
)


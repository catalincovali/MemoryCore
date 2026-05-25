package com.catalincovali.memorycore

import androidx.compose.ui.graphics.Color
import com.catalincovali.memorycore.ui.theme.ColorBlue
import com.catalincovali.memorycore.ui.theme.ColorCyan
import com.catalincovali.memorycore.ui.theme.ColorGreen
import com.catalincovali.memorycore.ui.theme.ColorMagenta
import com.catalincovali.memorycore.ui.theme.ColorRed
import com.catalincovali.memorycore.ui.theme.ColorYellow

val COLORS: List<Pair<String, Color>> = listOf(
    "R" to ColorRed,
    "G" to ColorGreen,
    "B" to ColorBlue,
    "M" to ColorMagenta,
    "Y" to ColorYellow,
    "C" to ColorCyan
)

enum class GamePhase {
    IDLE,
    COMPUTER_TURN,
    PAUSED,
    PLAYER_TURN,
    GAME_OVER
}


data class Game(
    val id: Long = 0,
    val maxCorrectLength: Int,
    val errorSequence: List<String>,
    val errorIndex: Int
)

data class GameUiState(
    val phase: GamePhase = GamePhase.IDLE,
    val computerSequence: List<String> = emptyList(),
    val playerInput: List<String> = emptyList(),
    val highlightedColor: String? = null,
    val errorIndex: Int? = null
)

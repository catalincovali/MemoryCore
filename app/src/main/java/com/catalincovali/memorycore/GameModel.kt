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

// stati della schermata di gioco
enum class GamePhase {
    IDLE,
    COMPUTER_TURN,
    PAUSED,
    PLAYER_TURN,
    GAME_OVER   // da `GAME_OVER` si esce solo con Back
}


data class Game(
    val id: Long = 0,
    val maxCorrectLength: Int,
    val errorSequence: List<String>,
    // diverso da `maxCorrectLength` quando si esce dal
    // gioco durante la partita
    val errorIndex: Int
)

data class GameUiState(
    val phase: GamePhase = GamePhase.IDLE,
    val computerSequence: List<String> = emptyList(),
    val playerInput: List<String> = emptyList(),
    // feedback visivo pressione bottoni
    val highlightedColor: String? = null,
    val errorIndex: Int? = null,
    // indice del tono attualmente in riproduzione (computer turn)
    // -1 = ancora nessun tono
    val currentStep: Int = -1
)

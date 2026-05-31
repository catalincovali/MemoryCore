package com.catalincovali.memorycore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catalincovali.memorycore.data.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GameViewModel(
    // `private`, non devono diventare property
    private val savedStateHandle: SavedStateHandle,
    private val repository: GameRepository
) : ViewModel() {

    val games: StateFlow<List<Game>> = repository.games
        .stateIn(
            scope = viewModelScope,
            // 5 secondi di tolleranza per i cambi di configurazione
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // mutabile interno, immutabile esposto fuori
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var computerJob: Job? = null


    private companion object {
        const val KEY_PHASE = "phase"
        const val KEY_COMPUTER_SEQ = "computer_seq"
        const val KEY_PLAYER_INPUT = "player_input"
        const val KEY_ERROR_INDEX = "error_index"
        const val KEY_CURRENT_STEP = "current_step"
    }

    // inizializzazione del ViewModel
    // ===============================================================================================
    init {
        restoreFromSavedState()
    }

    private fun restoreFromSavedState() {
        val savedPhase = savedStateHandle.get<String>(KEY_PHASE) ?: return
        val phase = GamePhase.valueOf(savedPhase)
        val computerSeq = savedStateHandle.get<ArrayList<String>>(KEY_COMPUTER_SEQ) ?: return
        val playerInput = savedStateHandle.get<ArrayList<String>>(KEY_PLAYER_INPUT) ?: arrayListOf()
        val errorIndex = savedStateHandle.get<Int>(KEY_ERROR_INDEX)
        val currentStep = savedStateHandle.get<Int>(KEY_CURRENT_STEP) ?: -1

        _uiState.value = GameUiState(
            phase = phase,
            computerSequence = computerSeq.toList(),
            playerInput = playerInput.toList(),
            errorIndex = errorIndex,
            currentStep = currentStep
        )

        // in caso di interruzione durante la partita si
        // riparte dal tono successivo
        if (phase == GamePhase.COMPUTER_TURN) {
            playComputerSequence(startIndex = currentStep + 1)
        }
    }
    // ===============================================================================================

    fun startGame() {
        // controllo per tap multipli
        if (_uiState.value.phase != GamePhase.IDLE) return
        val first = COLORS.random().first
        _uiState.value = GameUiState(
            phase = GamePhase.COMPUTER_TURN,
            computerSequence = listOf(first)
        )
        persistState()
        playComputerSequence()
    }

    fun pause() {
        if (_uiState.value.phase != GamePhase.COMPUTER_TURN) return
        computerJob?.cancel()
        _uiState.value = _uiState.value.copy(phase = GamePhase.PAUSED, highlightedColor = null)
        persistState()
    }

    fun resume() {
        if (_uiState.value.phase != GamePhase.PAUSED) return
        // riprendiamo dal tono in pausa non dal successivo
        val resumeFrom = _uiState.value.currentStep.coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(phase = GamePhase.COMPUTER_TURN)
        persistState()
        playComputerSequence(startIndex = resumeFrom)
    }

    // cambio del pulsante in base alla fase
    fun togglePauseResume() {
        when (_uiState.value.phase) {
            GamePhase.COMPUTER_TURN -> pause()
            GamePhase.PAUSED -> resume()
            else -> Unit
        }
    }

    fun terminateGame() {
        val state = _uiState.value
        if (state.phase == GamePhase.IDLE || state.phase == GamePhase.GAME_OVER) return

        computerJob?.cancel()

        if (state.computerSequence.size > 1) {
            viewModelScope.launch {
                repository.insert(
                    Game(
                        id = 0,
                        maxCorrectLength = state.computerSequence.size - 1,
                        errorSequence = state.computerSequence,
                        errorIndex = state.playerInput.size
                    )
                )
            }
        }


        _uiState.value = _uiState.value.copy(phase = GamePhase.GAME_OVER, highlightedColor = null)
        persistState()
    }

    fun onColorPressed(color: String) {
        val state = _uiState.value
        // controllo per sicurezza
        if (state.phase != GamePhase.PLAYER_TURN) return

        // feedback visivo
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(highlightedColor = color)
            delay(180L)
            if (_uiState.value.highlightedColor == color)
                _uiState.value = _uiState.value.copy(highlightedColor = null)
        }

        val newInput = state.playerInput + color
        val expected = state.computerSequence[newInput.size - 1]

        // controllo input giocatore
        when {
            // 1) colore premuto è sbagliato
            color != expected -> {
                val errorIdx = newInput.size - 1
                viewModelScope.launch {
                    repository.insert(
                        Game(
                            id = 0,
                            maxCorrectLength = state.computerSequence.size - 1,
                            errorSequence = state.computerSequence,
                            errorIndex = errorIdx
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    phase = GamePhase.GAME_OVER,
                    playerInput = newInput,
                    errorIndex = errorIdx
                )
                persistState()
            }

            // 2) colore premuto è giusto -> tocca al computer
            newInput.size == state.computerSequence.size -> {
                val nextSeq = state.computerSequence + COLORS.random().first
                _uiState.value = _uiState.value.copy(
                    phase = GamePhase.COMPUTER_TURN,
                    computerSequence = nextSeq,
                    playerInput = emptyList()
                )
                persistState()


                viewModelScope.launch {
                    delay(900L) // player turn -> computer turn, pausa maggiore
                    playComputerSequence()
                }
            }

            // 3) colore premuto è giusto -> tocca ancora al player
            else -> {
                _uiState.value = _uiState.value.copy(playerInput = newInput)
                persistState()
            }
        }
    }

    fun resetGame() {
        computerJob?.cancel()
        _uiState.value = GameUiState()
        clearPersistedState()
    }

    private fun playComputerSequence(startIndex: Int = 0) {
        val sequence = _uiState.value.computerSequence
        computerJob = viewModelScope.launch {
            for (i in startIndex until sequence.size) {
                // salvo il tono (indice) che andro ad usare
                _uiState.value = _uiState.value.copy(
                    currentStep = i,
                    highlightedColor = sequence[i]
                )
                savedStateHandle[KEY_CURRENT_STEP] = i
                delay(600L)
                _uiState.value = _uiState.value.copy(highlightedColor = null)
                delay(200L)
            }
            // sequenza completata torno al turno del giocatore
            _uiState.value = _uiState.value.copy(
                phase = GamePhase.PLAYER_TURN,
                highlightedColor = null,
                // indice tono, -1 = ancora nessun tono
                currentStep = -1
            )
            persistState()
        }
    }

    private fun persistState() {
        val s = _uiState.value
        savedStateHandle[KEY_PHASE] = s.phase.name
        savedStateHandle[KEY_COMPUTER_SEQ] = ArrayList(s.computerSequence)
        savedStateHandle[KEY_PLAYER_INPUT] = ArrayList(s.playerInput)
        savedStateHandle[KEY_CURRENT_STEP] = s.currentStep
        s.errorIndex?.let { savedStateHandle[KEY_ERROR_INDEX] = it }
    }

    private fun clearPersistedState() {
        savedStateHandle.remove<String>(KEY_PHASE)
        savedStateHandle.remove<ArrayList<String>>(KEY_COMPUTER_SEQ)
        savedStateHandle.remove<ArrayList<String>>(KEY_PLAYER_INPUT)
        savedStateHandle.remove<Int>(KEY_ERROR_INDEX)
        savedStateHandle.remove<Int>(KEY_CURRENT_STEP)
    }
}

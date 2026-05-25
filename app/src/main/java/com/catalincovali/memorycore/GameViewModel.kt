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
    private val savedStateHandle: SavedStateHandle,
    private val repository: GameRepository
) : ViewModel() {

    val games: StateFlow<List<Game>> = repository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var computerJob: Job? = null


    private companion object {
        const val KEY_PHASE = "phase"
        const val KEY_COMPUTER_SEQ = "computer_seq"
        const val KEY_PLAYER_INPUT = "player_input"
        const val KEY_ERROR_INDEX = "error_index"
    }

    init {
        restoreFromSavedState()
    }

    private fun restoreFromSavedState() {
        val savedPhase = savedStateHandle.get<String>(KEY_PHASE) ?: return
        val phase = GamePhase.valueOf(savedPhase)
        val computerSeq = savedStateHandle.get<ArrayList<String>>(KEY_COMPUTER_SEQ) ?: return
        val playerInput = savedStateHandle.get<ArrayList<String>>(KEY_PLAYER_INPUT) ?: arrayListOf()
        val errorIndex = savedStateHandle.get<Int>(KEY_ERROR_INDEX)

        val restoredPhase = if (phase == GamePhase.COMPUTER_TURN) GamePhase.PAUSED else phase

        _uiState.value = GameUiState(
            phase = restoredPhase,
            computerSequence = computerSeq.toList(),
            playerInput = playerInput.toList(),
            errorIndex = errorIndex
        )
    }

    fun startGame() {
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
        _uiState.value = _uiState.value.copy(phase = GamePhase.COMPUTER_TURN)
        persistState()
        playComputerSequence()
    }

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
        if (state.phase != GamePhase.PLAYER_TURN) return

        val newInput = state.playerInput + color
        val expected = state.computerSequence[newInput.size - 1]

        when {
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

                _uiState.value = state.copy(
                    phase = GamePhase.GAME_OVER,
                    playerInput = newInput,
                    errorIndex = errorIdx
                )
                persistState()
            }

            newInput.size == state.computerSequence.size -> {
                val nextSeq = state.computerSequence + COLORS.random().first
                _uiState.value = state.copy(
                    phase = GamePhase.COMPUTER_TURN,
                    computerSequence = nextSeq,
                    playerInput = emptyList()
                )
                persistState()
                playComputerSequence()
            }

            else -> {
                _uiState.value = state.copy(playerInput = newInput)
                persistState()
            }
        }
    }

    fun resetGame() {
        computerJob?.cancel()
        _uiState.value = GameUiState()
        clearPersistedState()
    }

    private fun playComputerSequence() {
        val sequence = _uiState.value.computerSequence
        computerJob = viewModelScope.launch {
            for (color in sequence) {
                _uiState.value = _uiState.value.copy(highlightedColor = color)
                delay(600L)
                _uiState.value = _uiState.value.copy(highlightedColor = null)
                delay(200L)
            }
            _uiState.value =
                _uiState.value.copy(phase = GamePhase.PLAYER_TURN, highlightedColor = null)
            persistState()
        }
    }

    private fun persistState() {
        val s = _uiState.value
        savedStateHandle[KEY_PHASE] = s.phase.name
        savedStateHandle[KEY_COMPUTER_SEQ] = ArrayList(s.computerSequence)
        savedStateHandle[KEY_PLAYER_INPUT] = ArrayList(s.playerInput)
        s.errorIndex?.let { savedStateHandle[KEY_ERROR_INDEX] = it }
    }

    private fun clearPersistedState() {
        savedStateHandle.remove<String>(KEY_PHASE)
        savedStateHandle.remove<ArrayList<String>>(KEY_COMPUTER_SEQ)
        savedStateHandle.remove<ArrayList<String>>(KEY_PLAYER_INPUT)
        savedStateHandle.remove<Int>(KEY_ERROR_INDEX)
    }
}

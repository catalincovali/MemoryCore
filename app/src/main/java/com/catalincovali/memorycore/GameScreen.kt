package com.catalincovali.memorycore

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catalincovali.memorycore.ui.theme.*


@Composable
fun GameScreen(
    uiState: GameUiState,
    onColorPressed: (String) -> Unit,
    onStart: () -> Unit,
    onPauseResume: () -> Unit,
    onTerminate: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val displayedSequence: List<String> = when (uiState.phase) {
        GamePhase.PLAYER_TURN -> uiState.playerInput
        GamePhase.GAME_OVER -> uiState.computerSequence
        else -> emptyList()
    }

    BackHandler(enabled = uiState.phase != GamePhase.IDLE) {
        if (uiState.phase != GamePhase.GAME_OVER) {
            onTerminate()
        }
        onNavigateBack()
    }

    if (isLandscape) {
        Row(
            Modifier
                .fillMaxHeight()
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorGrid(
                colors = COLORS,
                highlighted = uiState.highlightedColor,
                enabled = uiState.phase == GamePhase.PLAYER_TURN,
                onColorClick = onColorPressed,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            )
            Column(
                Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
            ) {
                SequenceText(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxWidth(),
                    sequence = displayedSequence
                )

                GameControls(
                    phase = uiState.phase,
                    onStart = onStart,
                    onPauseResume = onPauseResume,
                    onTerminate = onTerminate,
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth()
                )


            }
        }
    } else {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorGrid(
                colors = COLORS,
                highlighted = uiState.highlightedColor,
                enabled = uiState.phase == GamePhase.PLAYER_TURN,
                onColorClick = onColorPressed,
                modifier = Modifier
                    .weight(7f)
                    .fillMaxWidth(),
            )
            SequenceText(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                sequence = displayedSequence
            )
            GameControls(
                phase = uiState.phase,
                onStart = onStart,
                onPauseResume = onPauseResume,
                onTerminate = onTerminate,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )


        }
    }
}

@Composable
fun GameControls(
    phase: GamePhase,
    onStart: () -> Unit,
    onPauseResume: () -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onStart,
            enabled = phase == GamePhase.IDLE
        ) { Text(stringResource(R.string.start_button)) }

        val pauseLabel = if (phase == GamePhase.PAUSED)
            R.string.resume_button
        else
            R.string.pause_button
        Button(
            onClick = onPauseResume,
            enabled = phase == GamePhase.COMPUTER_TURN || phase ==
                    GamePhase.PAUSED
        ) { Text(stringResource(pauseLabel)) }

        Button(
            onClick = onTerminate,
            enabled = phase != GamePhase.IDLE && phase !=
                    GamePhase.GAME_OVER
        ) { Text(stringResource(R.string.end_button)) }

    }
}


@Composable
fun ColorGrid(
    colors: List<Pair<String, Color>>,
    highlighted: String? = null,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onColorClick: (String) -> (Unit),
) {
    Surface(
        modifier = modifier,
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(38.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(top = 15.dp, bottom = 15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colors.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp, end = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (letter, color) ->
                        Surface(
                            onClick = {
                                onColorClick(letter)
                            },
                            enabled = enabled,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(30.dp),
                            color = color,
                            shadowElevation = 10.dp,
                            border = BorderStroke(
                                width = if (letter == highlighted) 8.dp else 3.dp,
                                color = if (letter == highlighted)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                            )


                        ) {
                            Text(
                                text = letter,
                                color = color.copy(
                                    red = color.red * 0.8f,
                                    green = color.green * 0.8f,
                                    blue = color.blue * 0.8f,
                                    alpha = color.alpha
                                ),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(bottom = 16.dp, end = 20.dp)
                                    .wrapContentSize(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SequenceText(
    modifier: Modifier = Modifier,
    sequence: List<String>,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        border = BorderStroke(
            3.dp,
            MaterialTheme.colorScheme.background
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.sequence_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${sequence.size}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = 20.dp, top = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
            Text(
                text = sequence.joinToString(", "),
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(top = 10.dp, start = 20.dp, bottom = 20.dp, end = 12.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameScreenPlayerTurnPreview() {
    MemoryCoreTheme {
        GameScreen(
            uiState = GameUiState(
                phase = GamePhase.PLAYER_TURN,
                computerSequence = listOf("R", "G", "B", "Y"),
                playerInput = listOf("R", "G"),
            ),
            onColorPressed = {},
            onStart = {},
            onPauseResume = {},
            onTerminate = {},
            onNavigateBack = {}
        )
    }
}

@Preview(
    name = "Landscape — computer turn",
    showBackground = true,
    widthDp = 720,
    heightDp = 360
)
@Composable
fun GameScreenComputerTurnPreview() {
    MemoryCoreTheme {
        GameScreen(
            uiState = GameUiState(
                phase = GamePhase.COMPUTER_TURN,
                computerSequence = listOf("R", "G", "B"),
                highlightedColor = "G"
            ),
            onColorPressed = {},
            onStart = {},
            onPauseResume = {},
            onTerminate = {},
            onNavigateBack = {}
        )
    }
}

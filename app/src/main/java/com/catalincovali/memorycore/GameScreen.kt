package com.catalincovali.memorycore

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catalincovali.memorycore.ui.theme.*


val colors = listOf(
    "R" to ColorRed,
    "G" to ColorGreen,
    "B" to ColorBlue,
    "M" to ColorMagenta,
    "Y" to ColorYellow,
    "C" to ColorCyan
)

@Composable
fun GameScreen(
    sequence: List<String>,
    onAdd: (String) -> Unit,
    onClear: () -> Unit,
    onGameOver: () -> Unit
) {

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            Modifier
                .fillMaxHeight()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorGrid(
                colors,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                onColorClick = onAdd
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
                    sequence = sequence.joinToString(", ")

                )
                ActionButtons(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth(),
                    onClear = onClear,
                    onEndGame = onGameOver
                )
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorGrid(
                colors,
                modifier = Modifier
                    .weight(7f)
                    .fillMaxWidth(),
                onColorClick = onAdd
            )
            SequenceText(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                sequence = sequence.joinToString(", ")
            )
            ActionButtons(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                onClear = onClear,
                onEndGame = onGameOver
            )
        }
    }
}

@Composable
fun ColorGrid(
    colors: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    onColorClick: (String) -> (Unit)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (letter, color) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(color)
                            .border(
                                1.5.dp,
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onColorClick(letter)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            letter,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SequenceText(
    modifier: Modifier = Modifier,
    sequence: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = sequence,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    onEndGame: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onClear) { Text(stringResource(R.string.clear_button)) }
        Button(onClick = onEndGame) { Text(stringResource(R.string.end_game_button)) }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MemoryCoreTheme {
        GameScreen(
            sequence = listOf(
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E"
            ),
            onAdd = {},
            onClear = {},
            onGameOver = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 360)
@Composable
fun GameScreenLandscapePreview() {
    MemoryCoreTheme {
        GameScreen(
            sequence = listOf(
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E",
                "A",
                "B",
                "C",
                "D",
                "E"
            ),
            onAdd = {},
            onClear = {},
            onGameOver = {}
        )
    }
}
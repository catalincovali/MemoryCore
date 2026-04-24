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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    sequence = sequence
                )
                ActionButtons(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth(),
                    onClear = onClear,
                    onEndGame = onGameOver,
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
                colors,
                modifier = Modifier
                    .weight(7f)
                    .fillMaxWidth(),
                onColorClick = onAdd
            )
            SequenceText(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                sequence = sequence
            )
            ActionButtons(
                modifier = Modifier
                    .weight(1f)
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
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(30.dp),
                            color = color,
                            shadowElevation = 10.dp,
                            border = BorderStroke(
                                3.dp,
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
        OutlinedButton(onClick = onClear) { Text(stringResource(R.string.clear_button)) }
        Button(onClick = onEndGame) {
            Text(
                stringResource(R.string.end_game_button),
                fontWeight = FontWeight.Bold
            )
        }
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
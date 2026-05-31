package com.catalincovali.memorycore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme


// lista partite, schermata iniziale dell'applicazione
@Composable
fun GameList(
    games: List<Game>,
    onStartGame: () -> Unit,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartGame,
                icon = {
                    Text("▶")
                },
                text = { Text(stringResource(R.string.start_button)) },
                        containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary

            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            // header con il numero totale di partite tra parentesi
            Text(
                text = "${stringResource(R.string.list_title)} (${games.size})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
            )
            // lista effettiva delle partite
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameRow(
                        game = game,
                        onClick = { onGameClick(game.id) }
                    )
                }

            }
        }
    }
}

//riga della lista
//conteggio a sinistra, sequenza a destra con ellipsis
@Composable
private fun GameRow(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = game.maxCorrectLength.toString().padStart(2, '0'),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = buildErrorSequence(
                sequence = game.errorSequence,
                errorIndex = game.errorIndex,
                errorColor = MaterialTheme.colorScheme.error
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}

internal fun buildErrorSequence(
    sequence: List<String>,
    errorIndex: Int,
    errorColor: Color
) = buildAnnotatedString {
    sequence.forEachIndexed { i, letter ->
        if (i > 0) append(", ")
        if (i >= errorIndex) {
            // dall'errore in poi color di rosso
            withStyle(SpanStyle(color = errorColor)) { append(letter) }
        } else {
            append(letter)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameListPreview() {
    MemoryCoreTheme {
        GameList(
            games = listOf(
                Game(
                    maxCorrectLength = 4, errorSequence = listOf("R", "G", "B", "Y", "M"),
                    errorIndex = 4
                ),
                Game(
                    maxCorrectLength = 2, errorSequence = listOf("R", "G", "B"),
                    errorIndex = 2
                ),
                Game(
                    maxCorrectLength = 0, errorSequence = listOf("R"), errorIndex =
                        0
                ),
                Game(
                    maxCorrectLength = 9,
                    errorSequence =
                        listOf("R", "G", "B", "Y", "M", "C", "R", "G", "B", "Y", "M"),
                    errorIndex = 9
                )
            ),
            onStartGame = {},
            onGameClick = {}
        )
    }
}

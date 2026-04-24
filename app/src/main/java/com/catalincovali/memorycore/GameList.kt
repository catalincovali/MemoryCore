package com.catalincovali.memorycore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.res.stringResource


@Composable
fun GameList(
    games: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        border = BorderStroke(
            3.dp,
            MaterialTheme.colorScheme.background
        )
    )
    {
        Column(modifier = modifier.fillMaxSize()) {
            Text(
                text = "${stringResource(R.string.list_title)} (${games.size})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 20.dp, top = 20.dp)
            )




                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(games) { index, game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${(index + 1).toString().padStart(2, '0')}.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(40.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Text(
                                text = game.size.toString().padStart(2, '0'),
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold

                            )
                            Text(
                                text = game.joinToString(", "),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            )
                        }

                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameListPreview() {
    MemoryCoreTheme {
        GameList(
            games = listOf(
                listOf("A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf(
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
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"),
                listOf("A", "B", "C", "D", "E", "A", "B", "C", "D", "E")
            )
        )
    }
}
package com.catalincovali.memorycore

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme

@Composable
fun GameList(
    games: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Games (${games.size})",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )


        if (games.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No games yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(games) { game ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.size.toString(),
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
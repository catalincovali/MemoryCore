package com.catalincovali.memorycore

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.catalincovali.memorycore.ui.theme.*

@Composable
fun GameScreen() {

    val colors = listOf(
        "R" to ColorRed,
        "G" to ColorGreen,
        "B" to ColorBlue,
        "M" to ColorMagenta,
        "Y" to ColorYellow,
        "C" to ColorCyan
    )

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(Modifier.fillMaxSize().padding(8.dp)) {
            ColorGrid(colors)
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                SequenceText()
                ActionButtons()
            }
        }
    } else {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            ColorGrid(colors)
            SequenceText()
            ActionButtons()
        }
    }
}

@Composable
fun ColorGrid(colors: List<Pair<String, Color>>) {
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        colors.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { (letter, color) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(letter, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SequenceText() {
    Text(
        text = "R, G, B, Y",   // placeholder

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = {}) { Text("Clear") }
        Button(onClick = {}) { Text("End game") }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MemoryCoreTheme {
        GameScreen()
    }
}
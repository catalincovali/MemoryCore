package com.catalincovali.memorycore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme


@Composable
fun GameDetail(
    game: Game,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text(
            text = stringResource(R.string.detail_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = game.maxCorrectLength.toString().padStart(2, '0'),
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            )
            Text(
                text = buildErrorSequence(
                    sequence = game.errorSequence,
                    errorIndex = game.errorIndex,
                    errorColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameDetailPreview() {
    MemoryCoreTheme {
        GameDetail(
            game = Game(
                id = 1,
                maxCorrectLength = 12,
                errorSequence =
                    listOf("R", "G", "B", "Y", "M", "C", "R", "G", "B", "Y", "M", "C", "R"),
                errorIndex = 12
            )

        )
    }
}

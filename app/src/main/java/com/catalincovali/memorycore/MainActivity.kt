package com.catalincovali.memorycore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryCoreTheme {
                val navController = rememberNavController()

                var currentSequence by rememberSaveable { mutableStateOf(listOf<String>()) }
                var games by rememberSaveable { mutableStateOf(listOf<List<String>>()) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "game",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("game") {
                            GameScreen(
                                sequence = currentSequence,
                                onAdd = { letter -> currentSequence = currentSequence + letter },
                                onClear = {
                                    currentSequence = emptyList()
                                },
                                onGameOver = {
                                    games = games + listOf(currentSequence)
                                    currentSequence = emptyList()
                                    navController.navigate("gamelist") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable("gamelist") {
                            GameList(games = games)
                        }
                    }
                }
            }
        }
    }
}



package com.catalincovali.memorycore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.catalincovali.memorycore.ui.theme.MemoryCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryCoreTheme {

                val app = LocalContext.current.applicationContext as MemoryCoreApp
                val viewModel: GameViewModel = viewModel(
                    factory = GameViewModelFactory(app.gameRepository)
                )
                val uiState by viewModel.uiState.collectAsState()
                val games by viewModel.games.collectAsState()


                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "gamelist",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("gamelist") {
                            GameList(
                                games = games,
                                onStartGame = {
                                    viewModel.resetGame()
                                    navController.navigate("game")
                                },
                                onGameClick = { gameId ->
                                    navController.navigate("detail/$gameId")
                                }

                            )
                        }
                        composable("game") {
                            GameScreen(
                                uiState = uiState,
                                onColorPressed = viewModel::onColorPressed,
                                onStart = viewModel::startGame,
                                onPauseResume = viewModel::togglePauseResume,
                                onTerminate = viewModel::terminateGame,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "detail/{gameId}",
                            arguments = listOf(
                                navArgument("gameId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val gameId =
                                backStackEntry.arguments?.getLong("gameId") ?: return@composable
                            val game = games.find { it.id == gameId }
                            if (game != null) {
                                GameDetail(game = game)
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
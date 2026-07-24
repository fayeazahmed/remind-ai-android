package com.ahmed.remindai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahmed.remindai.screen.AiChatScreen
import com.ahmed.remindai.screen.ReminderScreen
import com.ahmed.remindai.ui.theme.RemindAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            RemindAITheme {
                RemindAiApp()
            }
        }
    }
}

private sealed class AppScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Reminder : AppScreen(
        route = "reminders",
        title = "Reminders",
        icon = Icons.Default.Home
    )

    object AI : AppScreen(
        route = "ai",
        title = "AI",
        icon = Icons.AutoMirrored.Filled.Chat
    )
}

@Composable
fun RemindAiApp() {

    val navController = rememberNavController()

    val items = listOf(
        AppScreen.Reminder,
        AppScreen.AI
    )

    Scaffold(

        bottomBar = {

            NavigationBar {

                val navBackStackEntry by navController.currentBackStackEntryAsState()

                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->

                    NavigationBarItem(

                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == screen.route } == true,

                        onClick = {

                            navController.navigate(screen.route) {

                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {

                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },

                        label = {
                            Text(screen.title)
                        }
                    )
                }
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppScreen.Reminder.route
        ) {

            composable(AppScreen.Reminder.route) {

                ReminderScreen(
                    contentPadding = innerPadding
                )
            }

            composable(AppScreen.AI.route) {

                AiChatScreen(
                    contentPadding = innerPadding
                )
            }
        }
    }
}

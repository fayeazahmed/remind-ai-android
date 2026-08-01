package com.ahmed.remindai

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahmed.remindai.screen.AiChatScreen
import com.ahmed.remindai.screen.AuthScreen
import com.ahmed.remindai.screen.ReminderScreen
import com.ahmed.remindai.ui.theme.RemindAITheme
import com.ahmed.remindai.viewmodel.AuthViewModel
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()
        requestExactAlarmPermission()

        enableEdgeToEdge()

        setContent {
            RemindAITheme {
                RootApp()
            }
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun requestExactAlarmPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return
        }

        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {
            return
        }

        startActivity(
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                "package:$packageName".toUri()
            )
        )
    }
}

@Composable
fun RootApp(
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        RemindAiApp(onLogout = { authViewModel.logout() })
    } else {
        AuthScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindAiApp(onLogout: () -> Unit) {

    val navController = rememberNavController()

    val items = listOf(
        AppScreen.Reminder,
        AppScreen.AI
    )

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("RemindAI") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },

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

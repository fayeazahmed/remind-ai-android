package com.ahmed.remindai.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.ahmed.remindai.viewmodel.ReminderViewModel
import com.ahmed.remindai.component.EmptyReminderState
import com.ahmed.remindai.component.ReminderCard

@Composable
fun ReminderScreen(
    contentPadding: PaddingValues,
    viewModel: ReminderViewModel = viewModel()
) {

    var inputText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Good Afternoon 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "What should I remind you about?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(12.dp),
                placeholder = {
                    Text("Example: Remind me to call Mom tomorrow at 8 PM")
                },
                maxLines = 6
            )

            viewModel.errorMessage?.let {

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            FilledIconButton(
                modifier = Modifier.size(80.dp),
                onClick = {
                    Log.d("VOICE", "Start recording")
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = inputText.isNotBlank(),
            onClick = {

                viewModel.addReminder(inputText)
                inputText = ""

            }
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null
            )

            Text(
                text = " Create Reminder"
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Today's Reminders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {

            viewModel.isLoading && viewModel.reminders.isEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            viewModel.reminders.isEmpty() -> {

                EmptyReminderState(
                    modifier = Modifier.weight(1f),
                    onSuggestionClick = {
                        inputText = it
                    }
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = viewModel.reminders,
                        key = { it.id }
                    ) { reminder ->

                        ReminderCard(
                            reminder = reminder,
                            onDelete = {
                                viewModel.deleteReminder(reminder)
                            }
                        )
                    }
                }
            }
        }
    }
}

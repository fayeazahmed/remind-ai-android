package com.ahmed.remindai.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.remindai.model.Reminder

private val priorityLabels = mapOf(
    0 to "Default",
    1 to "Serious"
)

@Composable
fun ReminderCreatedDialog(
    reminder: Reminder,
    onDismiss: () -> Unit,
    onSave: (title: String, body: String, notifyAt: String?, priority: Int) -> Unit
) {

    var title by remember(reminder.id) { mutableStateOf(reminder.title) }
    var body by remember(reminder.id) { mutableStateOf(reminder.body) }
    var notifyAt by remember(reminder.id) { mutableStateOf(reminder.notifyAt.orEmpty()) }
    var priority by remember(reminder.id) { mutableIntStateOf(reminder.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reminder Created")
        },
        text = {
            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.height(12.dp)) {}

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Detail") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Column(modifier = Modifier.height(12.dp)) {}

                OutlinedTextField(
                    value = notifyAt,
                    onValueChange = { notifyAt = it },
                    label = { Text("Notify At") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.height(12.dp)) {}

                Text("Priority")

                Column(modifier = Modifier.height(6.dp)) {}

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    priorityLabels.forEach { (value, label) ->

                        FilterChip(
                            selected = priority == value,
                            onClick = { priority = value },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {

                    val newNotifyAt = notifyAt.ifBlank { null }

                    val changed = title != reminder.title ||
                            body != reminder.body ||
                            newNotifyAt != reminder.notifyAt ||
                            priority != reminder.priority

                    if (changed) {
                        onSave(title, body, newNotifyAt, priority)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        }
    )
}

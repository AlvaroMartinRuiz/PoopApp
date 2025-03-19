package es.uc3m.android.poopapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import es.uc3m.android.poopapp.data.model.BristolType
import es.uc3m.android.poopapp.data.model.PoopLog

@Composable
fun AddPoopLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (PoopLog) -> Unit
) {
    var bristolScale by remember { mutableStateOf(4) }
    var duration by remember { mutableStateOf(5) }
    var strain by remember { mutableStateOf(3) }
    var completeness by remember { mutableStateOf(3) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Poop Log",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Bristol Scale Selection
                Text(
                    text = "Bristol Stool Scale",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = BristolType.values()[bristolScale - 1].description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = bristolScale.toFloat(),
                    onValueChange = { bristolScale = it.toInt() },
                    valueRange = 1f..7f,
                    steps = 5,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Duration Slider
                Text(
                    text = "Duration (minutes): $duration",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { duration = it.toInt() },
                    valueRange = 1f..30f,
                    steps = 29,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Strain Level
                Text(
                    text = "Strain Level: $strain",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = strain.toFloat(),
                    onValueChange = { strain = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Completeness
                Text(
                    text = "Completeness: $completeness",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = completeness.toFloat(),
                    onValueChange = { completeness = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                PoopLog(
                                    bristolScale = bristolScale,
                                    duration = duration,
                                    strain = strain,
                                    completeness = completeness,
                                    notes = notes
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
} 
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
import es.uc3m.android.poopapp.ui.theme.poopAppColors
import java.util.Date
import es.uc3m.android.poopapp.firebase.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth


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
            color = MaterialTheme.poopAppColors.popupCardBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Poop Log",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.poopAppColors.textPrimary
                )

                // Bristol Scale Selection
                Text(
                    text = "Bristol Stool Scale",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.poopAppColors.textPrimary
                )
                
                Text(
                    text = BristolType.values()[bristolScale - 1].description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.poopAppColors.textPrimary
                )

                Slider(
                    value = bristolScale.toFloat(),
                    onValueChange = { bristolScale = it.toInt() },
                    valueRange = 1f..7f,
                    steps = 5,
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.poopAppColors.buttonBackground,
                        activeTrackColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )

                // Duration Slider
                Text(
                    text = "Duration (minutes): $duration",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.poopAppColors.textPrimary
                )
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { duration = it.toInt() },
                    valueRange = 1f..30f,
                    steps = 29,
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.poopAppColors.buttonBackground,
                        activeTrackColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )

                // Strain Level
                Text(
                    text = "Strain Level: $strain",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.poopAppColors.textPrimary
                )
                Slider(
                    value = strain.toFloat(),
                    onValueChange = { strain = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.poopAppColors.buttonBackground,
                        activeTrackColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )

                // Completeness
                Text(
                    text = "Completeness: $completeness",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.poopAppColors.textPrimary
                )
                Slider(
                    value = completeness.toFloat(),
                    onValueChange = { completeness = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.poopAppColors.buttonBackground,
                        activeTrackColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.poopAppColors.textPrimary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null) {
                                val poopLog = PoopLog(
                                    userId = currentUser.uid,
                                    timestamp = Date(), // ✅ Assign current Firestore timestamp
                                    bristolScale = bristolScale,
                                    duration = duration,
                                    strain = strain,
                                    completeness = completeness,
                                    notes = notes
                                )
                                onConfirm(poopLog)
                                FirebaseRepository.savePoopLog(
                                    userId = currentUser.uid,
                                    poopLog = poopLog,
                                    onSuccess = {
                                        println(" Poop log saved successfully!")
                                    },
                                    onError = { error ->
                                        println(" Error saving poop log: $error")
                                    }
                                )
                            } else {
                                println(" No authenticated user found.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.poopAppColors.buttonBackground,
                            contentColor = MaterialTheme.poopAppColors.buttonContent
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
} 
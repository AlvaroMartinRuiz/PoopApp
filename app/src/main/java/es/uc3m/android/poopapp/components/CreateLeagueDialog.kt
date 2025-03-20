package es.uc3m.android.poopapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.android.poopapp.data.model.League
import es.uc3m.android.poopapp.firebase.FirebaseManager
import es.uc3m.android.poopapp.ui.theme.poopAppColors
import kotlinx.coroutines.launch

@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onLeagueCreated: (League) -> Unit // 🔥 Callback to return the created league
) {
    var leagueName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val firebaseManager = FirebaseManager()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Create New League", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = leagueName,
                    onValueChange = { leagueName = it },
                    label = { Text("League Name", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
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
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        color = MaterialTheme.poopAppColors.buttonBackground
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (leagueName.isNotBlank()) {
                        isLoading = true
                        coroutineScope.launch {
                            firebaseManager.createLeague(
                                name = leagueName,
                                createdBy = currentUserId,
                                onSuccess = { newLeague ->
                                    isLoading = false
                                    onLeagueCreated(newLeague) // 🔥 Pass created league back
                                    onDismiss() // Close dialog
                                },
                                onError = { error ->
                                    isLoading = false
                                    println("❌ Error creating league: $error")
                                }
                            )
                        }
                    }
                },
                enabled = leagueName.isNotBlank() && !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

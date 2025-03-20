package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uc3m.android.poopapp.data.model.League
import es.uc3m.android.poopapp.data.model.LeaderboardEntry
import es.uc3m.android.poopapp.firebase.FirebaseManager
import es.uc3m.android.poopapp.ui.components.CreateLeagueDialog
import kotlinx.coroutines.launch
import es.uc3m.android.poopapp.ui.components.LeagueCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShitShareScreen() {
    var showCreateLeagueDialog by remember { mutableStateOf(false) }
    var selectedLeague by remember { mutableStateOf<League?>(null) }

    val leagues = remember { mutableStateListOf<League>() }
    val coroutineScope = rememberCoroutineScope()
    val firebaseManager = FirebaseManager()

    val currentUserId = firebaseManager.getCurrentUser()?.uid ?: ""

    LaunchedEffect(Unit) {
        leagues.addAll(firebaseManager.getLeagues()) // No need for extra coroutineScope.launch
    }

    val leaderboardEntries = remember { mutableStateListOf<LeaderboardEntry>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedLeague?.name ?: "Poop Leaderboard")
                },
                navigationIcon = {
                    if (selectedLeague != null) {
                        IconButton(onClick = { selectedLeague = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (selectedLeague == null) {
                        IconButton(onClick = { showCreateLeagueDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create League")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (selectedLeague != null) {
            LeaderboardView(
                leagueId = selectedLeague!!.id,
                currentUserId = currentUserId
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "Your Leagues",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(padding)
                ) {
                    items(leagues) { league ->
                        LeagueCard(
                            league = league,
                            isSelected = selectedLeague == league,
                            onSelect = { selectedLeague = league }
                        )
                    }
                }
            }
        }
    }

    // Show the CreateLeagueDialog when needed
    if (showCreateLeagueDialog) {
        CreateLeagueDialog(
            onDismiss = { showCreateLeagueDialog = false },
            onLeagueCreated = { newLeague ->
                selectedLeague = newLeague // 🔥 Redirect to the created league
                showCreateLeagueDialog = false
            }
        )
    }

}

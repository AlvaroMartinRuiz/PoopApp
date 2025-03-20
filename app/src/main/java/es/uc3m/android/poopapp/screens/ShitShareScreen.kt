package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uc3m.android.poopapp.firebase.FirebaseManager
import es.uc3m.android.poopapp.data.model.League
import es.uc3m.android.poopapp.data.model.LeaderboardEntry
import es.uc3m.android.poopapp.ui.theme.poopAppColors
import kotlinx.coroutines.launch
import es.uc3m.android.poopapp.screens.LeaderboardView
import es.uc3m.android.poopapp.ui.components.CreateLeagueDialog


@Composable
fun ShitShareScreen(
    firebaseManager: FirebaseManager,
    userId: String
) {
    var selectedLeague by remember { mutableStateOf<League?>(null) }
    var showCreateLeagueDialog by remember { mutableStateOf(false) }
    var showJoinLeagueDialog by remember { mutableStateOf(false) }

    val leagues = remember { mutableStateListOf<League>() }
    val coroutineScope = rememberCoroutineScope()

    // Load leagues on first render
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            leagues.clear()
            leagues.addAll(firebaseManager.getLeaguesForUser(userId))
        }
    }

    // If a league is selected, show the LeagueDetailScreen; otherwise, show main screen
    if (selectedLeague != null) {
        LeagueDetailScreen(
            league = selectedLeague!!,
            onBackClick = { selectedLeague = null },
            userId = userId,
            firebaseManager = firebaseManager
        )
    } else {
        // Main ShitShare Screen
        Column(modifier = Modifier.fillMaxSize()) {
            // Title: "ShitShare" with consistent typography
            Text(
                text = "ShitShare",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )

            // List of leagues
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(leagues) { league ->
                    LeagueCard(
                        league = league,
                        onSelect = { selectedLeague = league }
                    )
                }
            }

            // Bottom Buttons: Create League & Join League with consistent styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showCreateLeagueDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.poopAppColors.buttonBackground,
                        contentColor = MaterialTheme.poopAppColors.buttonContent
                    )
                ) {
                    Text("Create League")
                }
                Button(
                    onClick = { showJoinLeagueDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.poopAppColors.buttonBackground,
                        contentColor = MaterialTheme.poopAppColors.buttonContent
                    )
                ) {
                    Text("Join League via Code")
                }
            }
        }
    }

    // Dialogs
    if (showCreateLeagueDialog) {
        CreateLeagueDialog(
            onDismiss = { showCreateLeagueDialog = false },
            onLeagueCreated = { newLeague ->
                selectedLeague = newLeague // 🔥 Redirect to the created league
                showCreateLeagueDialog = false
                coroutineScope.launch {
                    leagues.clear()
                    leagues.addAll(firebaseManager.getLeaguesForUser(userId)) // ✅ Refresh UI
                }
            }
        )
    }

    if (showJoinLeagueDialog) {
        JoinLeagueDialog(
            onDismiss = { showJoinLeagueDialog = false },
            onJoinLeague = { code ->
                coroutineScope.launch {
                    val league = firebaseManager.getLeagueByInviteCode(code)
                    if (league != null) {
                        firebaseManager.joinLeague(
                            userId = userId, 
                            league = league,
                            onSuccess = {
                                // Only refresh leagues after successful join
                                coroutineScope.launch {
                                    leagues.clear()
                                    leagues.addAll(firebaseManager.getLeaguesForUser(userId))
                                }
                                showJoinLeagueDialog = false
                            },
                            onError = { errorMsg ->
                                // Handle error (ideally show to user)
                                println("Error joining league: $errorMsg")
                                showJoinLeagueDialog = false
                            }
                        )
                    } else {
                        // League not found with that code
                        showJoinLeagueDialog = false
                    }
                }
            }
        )
    }
}

// -------------------------------------------------------------------
// LeagueCard
// -------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeagueCard(
    league: League,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.poopAppColors.staticCardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = league.name,
                color = MaterialTheme.poopAppColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${league.members.size} members",
                color = MaterialTheme.poopAppColors.textSecondary
            )
        }
    }
}

// -------------------------------------------------------------------
// LeagueDetailScreen
// -------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailScreen(
    league: League,
    onBackClick: () -> Unit,
    userId: String,
    firebaseManager: FirebaseManager
) {
    val coroutineScope = rememberCoroutineScope()
    val leaderboardEntries = remember { mutableStateListOf<LeaderboardEntry>() }

    // Fetch leaderboard entries for this league
    LaunchedEffect(league.id) {
        coroutineScope.launch {
            val entries = firebaseManager.getLeaderboardEntries(league.id)
            leaderboardEntries.clear()
            leaderboardEntries.addAll(entries)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = league.name,
                    color = MaterialTheme.colorScheme.onBackground
                ) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Show invite code so members can share it
            if (league.inviteCode?.isNotBlank() == true) {
                Text(
                    text = "Invite Code: ${league.inviteCode}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Leaderboard Title
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Leaderboard Items
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(leaderboardEntries) { index, entry ->
                    LeaderboardItem(
                        entry = entry,
                        rank = index + 1,
                        isCurrentUser = (entry.userId == userId)
                    )
                }
            }
        }
    }
}


// -------------------------------------------------------------------
// CreateLeagueDialog
// -------------------------------------------------------------------
@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onCreateLeague: (String) -> Unit
) {
    var leagueName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Create New League", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
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
                    cursorColor = MaterialTheme.poopAppColors.textPrimary
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreateLeague(leagueName) },
                enabled = leagueName.isNotBlank(),
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

// -------------------------------------------------------------------
// JoinLeagueDialog
// -------------------------------------------------------------------
@Composable
fun JoinLeagueDialog(
    onDismiss: () -> Unit,
    onJoinLeague: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Join League", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
            OutlinedTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it },
                label = { Text("Enter Invite Code", color = MaterialTheme.poopAppColors.textPrimary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                    unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                    focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                    unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                    cursorColor = MaterialTheme.poopAppColors.textPrimary
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onJoinLeague(inviteCode) },
                enabled = inviteCode.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Join")
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





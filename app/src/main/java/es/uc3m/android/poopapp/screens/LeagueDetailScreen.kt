package es.uc3m.android.poopapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.uc3m.android.poopapp.firebase.FirebaseManager
import es.uc3m.android.poopapp.data.model.LeaderboardEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailScreen(
    leagueId: String,
    navController: NavController,
    firebaseManager: FirebaseManager,
    userId: String
) {
    val leaderboardEntries = remember { mutableStateListOf<LeaderboardEntry>() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch leaderboard when the screen loads
    LaunchedEffect(leagueId) {
        coroutineScope.launch {
            val entries = firebaseManager.getLeaderboardEntries(leagueId)
            leaderboardEntries.clear()
            leaderboardEntries.addAll(entries)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyLeague") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB4D7D9),
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB4D7D9))
                .padding(paddingValues)
        ) {
            // League Leaderboard Section
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                itemsIndexed(leaderboardEntries) { index, entry ->
                    LeaderboardItem(
                        rank = index + 1,
                        entry = entry,
                        isCurrentUser = entry.userId == userId
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// LeaderboardItem (Reused from Previous Code)
// -------------------------------------------------------------------
@Composable
fun LeaderboardItem(
    rank: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#$rank",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(32.dp)
            )

            // Profile Picture (Placeholder)
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            // Username & Poop Count
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = entry.username,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${entry.poopCount} poops",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Rank Color Highlight
            Text(
                text = "${entry.poopCount} poops",
                fontWeight = FontWeight.Bold,
                color = when (rank) {
                    1 -> Color(0xFFFFD700) // Gold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> Color.Gray
                }
            )
        }
    }
}

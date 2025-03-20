package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uc3m.android.poopapp.data.model.LeaderboardEntry
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import es.uc3m.android.poopapp.firebase.FirebaseManager



@Composable
fun LeaderboardView(leagueId: String, currentUserId: String) {
    val leaderboardEntries = remember { mutableStateListOf<LeaderboardEntry>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(leagueId) {
        coroutineScope.launch {
            val firebaseManager = FirebaseManager()
            leaderboardEntries.addAll(firebaseManager.getLeaderboardEntries(leagueId))

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Leaderboard",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            itemsIndexed(leaderboardEntries) { index, entry ->
            LeaderboardItem(
                    entry = entry,
                    rank = index + 1,
                    isCurrentUser = entry.userId == currentUserId
                )
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    entry: LeaderboardEntry,
    rank: Int,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.username,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "${entry.poopCount} poops",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 
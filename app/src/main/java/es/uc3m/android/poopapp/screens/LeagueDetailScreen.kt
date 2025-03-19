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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailScreen(leagueId: String, navController: NavController) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                itemsIndexed(sampleLeaderboardItems) { index, item ->
                    LeaderboardItem(
                        rank = index + 1,
                        name = item.name,
                        points = item.points,
                        countryCode = item.countryCode
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    name: String,
    points: Int,
    countryCode: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                text = rank.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(24.dp)
            )

            // Profile Picture
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            // Name and Country
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = countryCode,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Points
            Text(
                text = "$points points",
                fontWeight = FontWeight.Bold,
                color = when(rank) {
                    1 -> Color(0xFFFFD700) // Gold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> Color.Gray
                }
            )
        }
    }
}

private data class LeaderboardEntry(
    val name: String,
    val points: Int,
    val countryCode: String
)

private val sampleLeaderboardItems = listOf(
    LeaderboardEntry("Davis Curtis", 2569, "🇺🇸"),
    LeaderboardEntry("Alena Danin", 1462, "🇺🇸"),
    LeaderboardEntry("Craig Gouse", 1053, "🇨🇦"),
    LeaderboardEntry("Madelyn Dias", 990, "🇵🇹"),
    LeaderboardEntry("Zain Vaccaro", 458, "🇺🇸"),
    LeaderboardEntry("Skylar Geidt", 448, "🇺🇸")
) 
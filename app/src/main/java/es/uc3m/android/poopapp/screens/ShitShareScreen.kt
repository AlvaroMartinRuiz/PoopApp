package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.android.poopapp.ui.theme.*

@Composable
fun ShitShareScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(16.dp)
    ) {
        Text(
            text = "ShitShare",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Leagues Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Leagues",
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                    Text(
                        text = "See all",
                        color = Gray,
                        modifier = Modifier.clickable { /* Handle see all click */ }
                    )
                }

                // League Items
                LeagueItem(
                    name = "MyLeague",
                    memberCount = "12 Friends joined",
                    onClick = { /* Handle league click */ }
                )
                LeagueItem(
                    name = "Family",
                    memberCount = "6 Friends joined",
                    onClick = { /* Handle league click */ }
                )
            }
        }

        // Friends Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Friends",
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(sampleFriends) { friend ->
                        FriendItem(
                            name = friend.name,
                            points = friend.points,
                            onClick = { /* Handle friend click */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueItem(
    name: String,
    memberCount: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LightBrown)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
            Text(
                text = memberCount,
                color = Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FriendItem(
    name: String,
    points: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LightBrown)
        )
        Text(
            text = name,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            color = DarkBrown
        )
        Text(
            text = "$points points",
            color = Gray
        )
    }
}

private data class Friend(
    val name: String,
    val points: Int
)

private val sampleFriends = listOf(
    Friend("Maren Workman", 328),
    Friend("Brandon Matrovs", 124),
    Friend("Manuela Lipshutz", 457)
) 
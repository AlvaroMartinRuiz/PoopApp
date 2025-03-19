package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import es.uc3m.android.poopapp.data.model.PoopStats
import es.uc3m.android.poopapp.firebase.AuthManager
import es.uc3m.android.poopapp.ui.components.AddPoopLogDialog
import es.uc3m.android.poopapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen() {
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Check authentication status
    val isAuthenticated = AuthManager.isAuthenticated
    
    if (!isAuthenticated) {
        // If not authenticated, show login prompt
        UnauthenticatedTrackerContent(
            onLoginClick = { AuthManager.requireLogin() }
        )
        return
    }
    
    // Temporary sample data
    val stats = PoopStats(
        currentStreak = 13,
        longestStreak = 20,
        todayCount = 2,
        weeklyStats = listOf(2, 3, 1, 4, 3, 2, 1),
        averageDuration = 7.5f,
        averageBristolScale = 4.2f,
        totalLogs = 145
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LightBrown
            ) {
                Icon(Icons.Default.Add, "Add Log", tint = DarkBrown)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Teal)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Screen Title
            Text(
                text = "Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // User Profile Section
            UserProfileSection(userName = AuthManager.currentUserDisplayName ?: "User")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Today's Stats Card
            TodayStatsCard(stats = stats)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Streaks Card
            StreaksCard(stats = stats)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weekly Activity Card
            WeeklyActivityCard(stats = stats)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional Stats Card
            AdditionalStatsCard(stats = stats)
        }
    }

    if (showAddDialog) {
        AddPoopLogDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { poopLog ->
                // TODO: Handle saving poop log
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun UnauthenticatedTrackerContent(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = DarkBrown,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Sign in to track your poop history",
            style = MaterialTheme.typography.titleMedium,
            color = DarkBrown,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Create an account to save your logs and view statistics across devices",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBrown.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBrown,
                contentColor = DarkBrown
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Login,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign In",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserProfileSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkBrown)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                color = DarkBrown
            )
            Text(
                text = "This week",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBrown.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TodayStatsCard(stats: PoopStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's Stats",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(6) { hour ->
                    TimeSlotColumn(
                        time = "${hour * 4}:00",
                        count = if (hour == 0) stats.todayCount else 0
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSlotColumn(time: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )
        CircularProgressIndicator(
            progress = count.toFloat() / 5f,
            modifier = Modifier.size(32.dp),
            color = DarkBrown,
            trackColor = LightBrown
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = DarkBrown.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StreaksCard(stats: PoopStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = LightBrown,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Current Streak", color = DarkBrown)
                Text("${stats.currentStreak} Days", fontWeight = FontWeight.Bold, color = DarkBrown)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Longest Streak", color = DarkBrown)
                Text("${stats.longestStreak} Days", fontWeight = FontWeight.Bold, color = DarkBrown)
            }
        }
    }
}

@Composable
private fun WeeklyActivityCard(stats: PoopStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkBrown
                )
                Text(
                    text = "Weekly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkBrown.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxValue = stats.weeklyStats.maxOrNull() ?: 1
                stats.weeklyStats.forEach { value ->
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height((100 * (value.toFloat() / maxValue)).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(DarkBrown)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkBrown.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdditionalStatsCard(stats: PoopStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Additional Statistics",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            StatRow("Average Duration", "${stats.averageDuration} min")
            StatRow("Average Bristol Scale", "Type ${stats.averageBristolScale}")
            StatRow("Total Logs", "${stats.totalLogs}")
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBrown.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )
    }
} 
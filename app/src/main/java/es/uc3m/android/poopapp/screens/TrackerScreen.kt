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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.android.poopapp.data.model.PoopLog
import es.uc3m.android.poopapp.data.model.PoopStats
import es.uc3m.android.poopapp.firebase.AuthManager
import es.uc3m.android.poopapp.firebase.FirebaseRepository
import es.uc3m.android.poopapp.ui.components.AddPoopLogDialog
import es.uc3m.android.poopapp.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen() {
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State for statistics
    var stats by remember { mutableStateOf<PoopStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Check authentication status
    val isAuthenticated = AuthManager.isAuthenticated

    // Load data on first composition
    LaunchedEffect(key1 = isAuthenticated) {
        if (isAuthenticated) {
            loadUserStats { loadedStats ->
                stats = loadedStats
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    if (!isAuthenticated) {
        // If not authenticated, show login prompt
        UnauthenticatedTrackerContent(
            onLoginClick = { AuthManager.requireLogin() }
        )
        return
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Teal)
        ) {
            if (isLoading) {
                // Loading state
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = DarkBrown
                )
            } else {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 80.dp)
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Display stats if available
                    stats?.let { poopStats ->
                        // Streaks Card
                        StreaksCard(stats = poopStats)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Monthly Calendar
                        MonthlyCalendarCard(stats = poopStats)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Weekly Activity Card
                        WeeklyActivityCard(stats = poopStats)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Additional Stats Card
                        AdditionalStatsCard(stats = poopStats)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPoopLogDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { poopLog ->
                coroutineScope.launch {
                    try {
                        // Add the log to Firebase
                        FirebaseRepository.addPoopLog(poopLog)

                        // Reload stats
                        loadUserStats { loadedStats ->
                            stats = loadedStats
                        }

                        showAddDialog = false
                    } catch (e: Exception) {
                        // Handle error
                        e.printStackTrace()
                    }
                }
            }
        )
    }
}

// Function to load user stats
private suspend fun loadUserStats(onStatsLoaded: (PoopStats) -> Unit) {
    try {
        // Get user streaks from Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        val (currentStreak, longestStreak) = FirebaseRepository.getUserStreaks()

        // Get user pooplogs
        val poopLogs = FirebaseRepository.getUserPoopLogs()

        // Calculate stats from logs
        val stats = PoopStats.calculateFromLogs(
            poopLogs = poopLogs,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )

        onStatsLoaded(stats)
    } catch (e: Exception) {
        // Handle error - return default stats
        e.printStackTrace()
        onStatsLoaded(PoopStats())
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
private fun MonthlyCalendarCard(stats: PoopStats) {
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthName = dateFormat.format(calendar.time)

    // Get the first day of month (0 = Sunday, 1 = Monday, etc.)
    val firstDayCalendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfMonthOffset = firstDayCalendar.get(Calendar.DAY_OF_WEEK) - 1 // Adjust to 0-based

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Calendar",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBrown,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = monthName,
                style = MaterialTheme.typography.titleSmall,
                color = DarkBrown,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Calendar header (S M T W T F S)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBrown,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val totalCells = firstDayOfMonthOffset + daysInMonth
            val rows = (totalCells + 6) / 7 // Ceiling division

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val index = row * 7 + col
                        val day = index - firstDayOfMonthOffset + 1

                        if (day in 1..daysInMonth) {
                            val hasVisit = stats.monthlyVisitDays.contains(day)
                            CalendarDay(day = day, hasVisit = hasVisit)
                        } else {
                            // Empty cell
                            Spacer(modifier = Modifier.width(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(day: Int, hasVisit: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (hasVisit) DarkBrown else Color.Transparent)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasVisit) White else DarkBrown,
            textAlign = TextAlign.Center
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
            // Current Streak (more prominent)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stats.currentStreak}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "days",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkBrown
                )
            }

            Divider(
                color = DarkBrown.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Longest Streak (less prominent)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Longest Streak",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkBrown
                )
                Text(
                    text = "${stats.longestStreak} days",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
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
                            .height((100 * (value.toFloat() / maxValue.coerceAtLeast(1))).dp)
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

            StatRow("Today's Count", "${stats.todayCount}")
            StatRow("Average Duration", "${stats.averageDuration.toInt()} min")
            StatRow("Average Bristol Scale", "Type ${String.format("%.1f", stats.averageBristolScale)}")
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
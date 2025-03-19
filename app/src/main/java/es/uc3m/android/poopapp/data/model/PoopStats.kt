package es.uc3m.android.poopapp.data.model

data class PoopStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val todayCount: Int = 0,
    val weeklyStats: List<Int> = List(7) { 0 }, // Count for each day of the week
    val averageDuration: Float = 0f,
    val averageBristolScale: Float = 0f,
    val totalLogs: Int = 0
) 
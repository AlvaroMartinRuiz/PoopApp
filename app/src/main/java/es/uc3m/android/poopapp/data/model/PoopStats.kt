package es.uc3m.android.poopapp.data.model

import java.util.Calendar
import java.util.Date

data class PoopStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val todayCount: Int = 0,
    val weeklyStats: List<Int> = List(7) { 0 }, // Count for each day of the week
    val averageDuration: Float = 0f,
    val averageBristolScale: Float = 0f,
    val totalLogs: Int = 0,
    val monthlyVisitDays: List<Int> = emptyList() // Days of month with bathroom visits
) {
    companion object {
        // Calculate stats from pooplogs
        fun calculateFromLogs(
            poopLogs: List<PoopLog>,
            currentStreak: Int = 0,
            longestStreak: Int = 0
        ): PoopStats {
            if (poopLogs.isEmpty()) {
                return PoopStats(
                    currentStreak = currentStreak,
                    longestStreak = longestStreak
                )
            }
            
            // Get total logs count
            val totalLogs = poopLogs.size
            
            // Calculate today's count
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            
            val todayCount = poopLogs.count { log ->
                val logDate = Calendar.getInstance().apply {
                    time = log.timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                logDate == today
            }
            
            // Calculate weekly stats
            val weeklyStats = calculateWeeklyStats(poopLogs)
            
            // Calculate average duration
            val averageDuration = if (poopLogs.isNotEmpty()) {
                poopLogs.map { it.duration }.average().toFloat()
            } else {
                0f
            }
            
            // Calculate average Bristol scale
            val averageBristolScale = if (poopLogs.isNotEmpty()) {
                poopLogs.map { it.bristolScale }.average().toFloat()
            } else {
                0f
            }
            
            // Get monthly visit days
            val monthlyVisitDays = calculateMonthlyVisitDays(poopLogs)
            
            return PoopStats(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                todayCount = todayCount,
                weeklyStats = weeklyStats,
                averageDuration = averageDuration,
                averageBristolScale = averageBristolScale,
                totalLogs = totalLogs,
                monthlyVisitDays = monthlyVisitDays
            )
        }
        
        // Calculate days per week with bathroom visits (last 7 days)
        private fun calculateWeeklyStats(poopLogs: List<PoopLog>): List<Int> {
            val weeklyStats = MutableList(7) { 0 }
            val today = Calendar.getInstance()
            
            // Get current day of week (Sunday = 1, Saturday = 7)
            val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK)
            
            // For each of the last 7 days
            for (i in 0 until 7) {
                val targetDay = Calendar.getInstance()
                targetDay.add(Calendar.DAY_OF_YEAR, -i)
                targetDay.set(Calendar.HOUR_OF_DAY, 0)
                targetDay.set(Calendar.MINUTE, 0)
                targetDay.set(Calendar.SECOND, 0)
                targetDay.set(Calendar.MILLISECOND, 0)
                
                val targetDate = targetDay.time
                
                // Count logs for this day
                val count = poopLogs.count { log ->
                    val logDate = Calendar.getInstance().apply {
                        time = log.timestamp
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                    
                    logDate == targetDate
                }
                
                // Map to correct index for our weekly stats
                // Our weeklyStats array goes from Sunday (0) to Saturday (6)
                val dayOfWeek = (targetDay.get(Calendar.DAY_OF_WEEK) - 1) // Convert to 0-based index
                weeklyStats[dayOfWeek] = count
            }
            
            return weeklyStats
        }
        
        // Calculate monthly visit days (for current month)
        private fun calculateMonthlyVisitDays(poopLogs: List<PoopLog>): List<Int> {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            return poopLogs
                .filter { log ->
                    val logCalendar = Calendar.getInstance().apply { time = log.timestamp }
                    logCalendar.get(Calendar.MONTH) == currentMonth && 
                    logCalendar.get(Calendar.YEAR) == currentYear
                }
                .map { log ->
                    Calendar.getInstance().apply { 
                        time = log.timestamp 
                    }.get(Calendar.DAY_OF_MONTH)
                }
                .distinct()
        }
    }
} 
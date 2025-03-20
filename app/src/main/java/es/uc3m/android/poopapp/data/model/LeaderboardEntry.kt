package es.uc3m.android.poopapp.data.model
import com.google.firebase.Timestamp

data class LeaderboardEntry(
    val userId: String = "",
    val username: String = "",
    val leagueId: String = "",
    val poopCount: Int = 0,
    val lastUpdated: Timestamp = Timestamp.now()
) 
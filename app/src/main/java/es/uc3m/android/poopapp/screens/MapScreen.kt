package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.android.poopapp.ui.theme.DarkBrown
import es.uc3m.android.poopapp.ui.theme.Gray
import es.uc3m.android.poopapp.ui.theme.Teal

@Composable
fun MapScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(16.dp)
    ) {
        Text(
            text = "Map",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Google Maps integration coming soon",
                    color = Gray
                )
            }
        }
    }
} 
package edu.unikom.culinarynight.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import edu.unikom.culinarynight.data.model.Review
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.draw.clip

@Composable
fun ReviewCard(review: Review) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = review.userName, style = MaterialTheme.typography.titleSmall)
                }
                Row {
                    repeat(5) { idx ->
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp),
                            tint = if (idx < review.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = review.rating.toString(), style = MaterialTheme.typography.bodySmall)
                }
            }

            if (review.komentar.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = review.komentar, style = MaterialTheme.typography.bodyMedium)
            }

            if (review.photoUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(review.photoUrl),
                    contentDescription = "Foto review",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            Text(text = formatter.format(Date(review.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

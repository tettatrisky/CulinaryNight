package edu.unikom.culinarynight.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import edu.unikom.culinarynight.data.model.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: suspend (Float, String, Uri?) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Review", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Rating")
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { idx ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = (idx + 1).toFloat() },
                            tint = if (idx < rating) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment, onValueChange = { comment = it },
                    placeholder = { Text("Tuliskan pengalaman Anda...") },
                    modifier = Modifier.fillMaxWidth(), minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { launcher.launch("image/*") }) { Text("Pilih Foto") }
                    Spacer(modifier = Modifier.width(12.dp))
                    imageUri?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // launch suspend onSubmit
                        scope.launch {
                            onSubmit(rating, comment, imageUri)
                        }
                    }, enabled = rating > 0f) {
                        Text("Kirim")
                    }
                }
            }
        }
    }
}

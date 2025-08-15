package edu.unikom.culinarynight.ui.screens.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.unikom.culinarynight.ui.viewmodel.ProfileUiState
import edu.unikom.culinarynight.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onVoucherClick: () -> Unit,
    onReviewClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogout: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { vm.onChangePhoto(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                },
                title = { Text("Profil") }
            )
        }
    ) { inner ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GreetingHeader(state)

            Spacer(Modifier.height(16.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                ProfilePhoto(
                    url = state.photoUrl,
                    size = 108
                )
                SmallFloatingActionButton(
                    onClick = { picker.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .offset(x = 6.dp, y = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Ganti Foto")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(state.email, style = MaterialTheme.typography.bodyMedium)

            if (state.uploading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth(0.6f))
            }

            Spacer(Modifier.height(24.dp))
            Divider()

            MenuItem(title = "Voucher Saya", onClick = onVoucherClick)
            MenuItem(title = "Review Saya", onClick = onReviewClick)
            MenuItem(title = "Tentang Aplikasi", onClick = onAboutClick)

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Keluar", color = MaterialTheme.colorScheme.onError)
            }

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                AssistChip(onClick = { /* dismiss? */ }, label = { Text(it) })
            }
        }
    }
}

@Composable
private fun GreetingHeader(state: ProfileUiState) {
    Text(
        text = "Halo, ${state.name} 👋",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Selamat datang di Culinary Night",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ProfilePhoto(url: String?, size: Int) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url ?: "about:blank")
            .crossfade(true)
            .build(),
        contentDescription = "Foto Profil",
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun MenuItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Rounded.ChevronRight, contentDescription = null)
    }
    Divider()
}

package edu.unikom.culinarynight.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Menggunakan AutoMirrored
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unikom.culinarynight.data.model.PKLData
import edu.unikom.culinarynight.ui.components.PKLCard
import edu.unikom.culinarynight.ui.components.SearchBar
import edu.unikom.culinarynight.viewmodel.PKLViewModel
import edu.unikom.culinarynight.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPKLDetail: (PKLData) -> Unit,
    onNavigateToVoucher: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    // --- PERUBAHAN UTAMA DI SINI ---
    // 1. Dapatkan context saat ini
    val context = LocalContext.current
    // 2. Buat ViewModel menggunakan Factory yang sudah dibuat
    val pklViewModel: PKLViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    // ---------------------------------

    val pklData by pklViewModel.pklData.collectAsState()
    val isLoading by pklViewModel.isLoading.collectAsState()
    val errorMessage by pklViewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredData = remember(pklData, searchQuery) {
        if (searchQuery.isBlank()) {
            pklData
        } else {
            pklViewModel.searchPKL(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Culinary Night",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PKL Terbaik di Bandung",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToVoucher) {
                        Icon(Icons.Default.LocalOffer, contentDescription = "Voucher")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { pklViewModel.loadPKLData() }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(16.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { pklViewModel.loadPKLData() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                filteredData.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🍜",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "Tidak ada data PKL"
                                else "Tidak ditemukan PKL untuk '$searchQuery'",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Ditemukan ${filteredData.size} lokasi PKL",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(filteredData) { pkl ->
                            PKLCard(
                                pklData = pkl,
                                onClick = { onNavigateToPKLDetail(pkl) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
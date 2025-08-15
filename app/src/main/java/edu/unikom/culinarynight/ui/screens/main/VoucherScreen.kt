package edu.unikom.culinarynight.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Menggunakan ikon AutoMirrored
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unikom.culinarynight.data.model.AuthState
import edu.unikom.culinarynight.data.model.UserVoucher
import edu.unikom.culinarynight.ui.components.VoucherCard
import edu.unikom.culinarynight.viewmodel.AuthViewModel
import edu.unikom.culinarynight.viewmodel.ViewModelFactory
import edu.unikom.culinarynight.viewmodel.VoucherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    onNavigateBack: () -> Unit,
    // ViewModel dihapus dari parameter
) {
    // --- PERBAIKAN UTAMA ADA DI SINI ---
    // 1. Dapatkan context
    val context = LocalContext.current
    // 2. Buat factory
    val factory = ViewModelFactory(context)
    // 3. Buat semua ViewModel menggunakan factory
    val voucherViewModel: VoucherViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    // ------------------------------------

    val availableVouchers by voucherViewModel.availableVouchers.collectAsState()
    val userVouchers by voucherViewModel.userVouchers.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tersedia", "Milik Saya")

    // Mengambil data saat composable pertama kali ditampilkan
    LaunchedEffect(authState) {
        voucherViewModel.loadAvailableVouchers()
        val currentAuth = authState
        if (currentAuth is AuthState.Authenticated) {
            voucherViewModel.loadUserVouchers(currentAuth.user.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Voucher UMKM") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Menggunakan ikon AutoMirrored
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> { // Tab "Tersedia"
                    if (availableVouchers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎟️", style = MaterialTheme.typography.displayMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Tidak ada voucher tersedia saat ini")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(availableVouchers) { voucher ->
                                val currentAuth = authState
                                VoucherCard(
                                    voucher = voucher,
                                    onClaim = {
                                        if (currentAuth is AuthState.Authenticated) {
                                            voucherViewModel.claimVoucher(
                                                currentAuth.user.id,
                                                voucher.id,
                                                onSuccess = { /* Tampilkan notifikasi sukses */ },
                                                onError = { /* Tampilkan notifikasi error */ }
                                            )
                                        } else {
                                            // Tampilkan pesan untuk login
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> { // Tab "Milik Saya"
                    val currentAuth = authState
                    if (currentAuth !is AuthState.Authenticated || userVouchers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎟️", style = MaterialTheme.typography.displayMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    if (currentAuth !is AuthState.Authenticated) "Silakan login untuk melihat voucher Anda"
                                    else "Anda belum memiliki voucher"
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(userVouchers) { userVoucher ->
                                // Asumsi UserVoucher memiliki detail Voucher di dalamnya
                                userVoucher.voucher?.let { voucher ->
                                    VoucherCard(
                                        voucher = voucher,
                                        isOwned = true,
                                        isUsed = userVoucher.isUsed,
                                        onClaim = null // Tidak ada aksi klaim di tab "Milik Saya"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
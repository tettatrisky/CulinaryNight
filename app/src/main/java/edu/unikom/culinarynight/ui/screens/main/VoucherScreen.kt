package edu.unikom.culinarynight.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unikom.culinarynight.data.model.AuthState
import edu.unikom.culinarynight.ui.components.VoucherCard
import edu.unikom.culinarynight.viewmodel.AuthViewModel
import edu.unikom.culinarynight.viewmodel.VoucherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    onNavigateBack: () -> Unit,
    voucherViewModel: VoucherViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val availableVouchers by voucherViewModel.availableVouchers.collectAsState()
    val userVouchers by voucherViewModel.userVouchers.collectAsState()
    val authState by authViewModel.authState.collectAsState() // Delegated property

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tersedia", "Milik Saya")

    LaunchedEffect(Unit) {
        // Fix here: Create a local variable for authState inside LaunchedEffect
        val currentAuthState = authState
        voucherViewModel.loadAvailableVouchers()
        if (currentAuthState is AuthState.Authenticated) {
            voucherViewModel.loadUserVouchers(currentAuthState.user.id) // Access user.id from local variable
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Voucher UMKM") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Tab Row
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
                0 -> {
                    // Available Vouchers
                    if (availableVouchers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🎟️",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Tidak ada voucher tersedia")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(availableVouchers) { voucher ->
                                VoucherCard(
                                    voucher = voucher,
                                    onClaim = {
                                        // Fix here: Create a local variable for authState inside lambda
                                        val currentUserAuthState = authState
                                        if (currentUserAuthState is AuthState.Authenticated) {
                                            voucherViewModel.claimVoucher(
                                                currentUserAuthState.user.id, // Access user.id from local variable
                                                voucher.id,
                                                onSuccess = { /* Show success */ },
                                                onError = { /* Show error */ }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // User Vouchers
                    if (userVouchers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🎟️",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Belum ada voucher yang diklaim")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(userVouchers) { userVoucher ->
                                userVoucher.voucher?.let { voucher ->
                                    VoucherCard(
                                        voucher = voucher,
                                        isOwned = true,
                                        isUsed = userVoucher.isUsed,
                                        onClaim = null
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
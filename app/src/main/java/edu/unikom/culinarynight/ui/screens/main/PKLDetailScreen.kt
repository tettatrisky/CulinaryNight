package edu.unikom.culinarynight.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unikom.culinarynight.data.model.*
import edu.unikom.culinarynight.ui.components.*
import edu.unikom.culinarynight.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PKLDetailScreen(
    lokasi: String,
    onNavigateBack: () -> Unit,
    pklViewModel: PKLViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    voucherViewModel: VoucherViewModel = viewModel(),
    crowdMeterViewModel: CrowdMeterViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val pklData by pklViewModel.pklData.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()
    val availableVouchers by voucherViewModel.availableVouchers.collectAsState()
    val crowdMeter by crowdMeterViewModel.crowdMeter.collectAsState()
    val authState by authViewModel.authState.collectAsState() // Delegated property

    val currentPKL = pklData.find { it.lokasi == lokasi }
    val locationVouchers = availableVouchers.filter { it.lokasiPkl == lokasi }

    var showReviewDialog by remember { mutableStateOf(false) }
    var showCrowdDialog by remember { mutableStateOf(false) }

    LaunchedEffect(lokasi) {
        reviewViewModel.loadReviews(lokasi)
        crowdMeterViewModel.loadCrowdLevel(lokasi)
        voucherViewModel.loadAvailableVouchers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail PKL") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentPKL == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("PKL tidak ditemukan")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // PKL Info Card
                item {
                    PKLInfoCard(pklData = currentPKL)
                }

                // Crowd Meter Card
                item {
                    CrowdMeterCard(
                        crowdMeter = crowdMeter,
                        onUpdateCrowd = { showCrowdDialog = true }
                    )
                }

                // Vouchers Section
                if (locationVouchers.isNotEmpty()) {
                    item {
                        VoucherSection(
                            vouchers = locationVouchers,
                            onClaimVoucher = { voucher ->
                                // Fix here: Create a local variable for authState
                                val currentUserAuthState = authState
                                if (currentUserAuthState is AuthState.Authenticated) {
                                    voucherViewModel.claimVoucher(
                                        currentUserAuthState.user.id, // Access user.id from local variable
                                        voucher.id,
                                        onSuccess = { /* Show success message */ },
                                        onError = { /* Show error message */ }
                                    )
                                }
                            }
                        )
                    }
                }

                // Reviews Section
                item {
                    ReviewSection(
                        reviews = reviews,
                        onAddReview = { showReviewDialog = true }
                    )
                }
            }
        }
    }

    // Review Dialog
    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                // Fix here: Create a local variable for authState
                val currentUserAuthState = authState
                if (currentUserAuthState is AuthState.Authenticated) {
                    val review = Review(
                        userId = currentUserAuthState.user.id, // Access user.id from local variable
                        userName = currentUserAuthState.user.name, // Access user.name from local variable
                        lokasiPkl = lokasi,
                        rating = rating,
                        komentar = comment
                    )
                    reviewViewModel.addReview(
                        review = review,
                        onSuccess = { showReviewDialog = false },
                        onError = { /* Show error */ }
                    )
                }
            }
        )
    }

    // Crowd Dialog
    if (showCrowdDialog) {
        CrowdUpdateDialog(
            currentLevel = crowdMeter?.level ?: CrowdLevel.LOW,
            onDismiss = { showCrowdDialog = false },
            onUpdate = { level ->
                // Fix here: Create a local variable for authState
                val currentUserAuthState = authState
                if (currentUserAuthState is AuthState.Authenticated) {
                    crowdMeterViewModel.updateCrowdLevel(
                        lokasi = lokasi,
                        level = level,
                        userId = currentUserAuthState.user.id, // Access user.id from local variable
                        onSuccess = { showCrowdDialog = false },
                        onError = { /* Show error */ }
                    )
                }
            }
        )
    }
}
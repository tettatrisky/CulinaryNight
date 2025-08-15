package edu.unikom.culinarynight.ui.screens.main

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // UPDATED ICON
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import edu.unikom.culinarynight.data.model.*
import edu.unikom.culinarynight.ui.components.*
import edu.unikom.culinarynight.viewmodel.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PKLDetailScreen(
    lokasi: String,
    onNavigateBack: () -> Unit,
    // --- VIEWMODELS ARE REMOVED FROM PARAMETERS ---
) {
    // --- THIS IS THE FIX ---
    // Create the factory and then all the ViewModels using that factory.
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    val pklViewModel: PKLViewModel = viewModel(factory = factory)
    val reviewViewModel: ReviewViewModel = viewModel(factory = factory)
    // val voucherViewModel: VoucherViewModel = viewModel(factory = factory) // Uncomment if needed
    val crowdMeterViewModel: CrowdMeterViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    // ----------------------

    // load state
    // Added initial values to prevent crashes before data loads
    val pklData by pklViewModel.pklData.collectAsState(initial = emptyList())
    val reviews by reviewViewModel.reviews.collectAsState(initial = emptyList())
    val crowdMeter by crowdMeterViewModel.crowdMeter.collectAsState(initial = null)
    val authState by authViewModel.authState.collectAsState()
    val crowdHistory by crowdMeterViewModel.crowdHistory.collectAsState(initial = emptyList())

    val currentPKL = pklData.find { it.lokasi == lokasi }

    var showReviewDialog by remember { mutableStateOf(false) }
    var showCrowdDialog by remember { mutableStateOf(false) }

    // when screen opens, load data
    LaunchedEffect(lokasi) {
        reviewViewModel.loadReviews(lokasi)
        crowdMeterViewModel.loadCrowdLevel(lokasi)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Detail PKL") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") // UPDATED ICON
                }
            }
        )
    }) { paddingValues ->
        if (currentPKL == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center
            ) {
                // Show a loading indicator while finding the PKL data
                CircularProgressIndicator()
                LaunchedEffect(Unit) {
                    pklViewModel.loadPKLData() // Attempt to load data if PKL is null
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PKLInfoCard(pklData = currentPKL)
            }

            item {
                // Map + Marker
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    update = { mapView ->
                        val lat = currentPKL.latitude ?: -6.914744
                        val lng = currentPKL.longitude ?: 107.609810
                        val geoPoint = GeoPoint(lat, lng)

                        mapView.controller.setZoom(16.0)
                        mapView.controller.setCenter(geoPoint)
                        mapView.overlays.clear()

                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.title = currentPKL.lokasi
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(marker)
                        mapView.invalidate() // Force redraw
                    }
                )
            }

            item {
                CrowdMeterCard(crowdMeter = crowdMeter, onUpdateCrowd = { showCrowdDialog = true })
            }

            item {
                // History Chart
                AndroidView(
                    factory = { ctx -> LineChart(ctx) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    update = { lineChart ->
                        val entries = crowdHistory.mapIndexed { idx, pair -> Entry(idx.toFloat(), pair.second.toFloat()) }
                        val dataSet = LineDataSet(entries, "Riwayat Keramaian")
                        dataSet.lineWidth = 2f
                        lineChart.data = LineData(dataSet)
                        lineChart.description.isEnabled = false
                        lineChart.invalidate()
                    }
                )
            }

            item {
                ReviewSection(reviews = reviews, onAddReview = { showReviewDialog = true })
            }
        }
    }

    // Dialogs remain the same...
    if (showReviewDialog) {
        ReviewDialog(onDismiss = { showReviewDialog = false }, onSubmit = { rating, comment, imageUri ->
            val currentUser = authState
            if (currentUser is AuthState.Authenticated) {
                val review = Review(
                    userId = currentUser.user.id,
                    userName = currentUser.user.name,
                    lokasiPkl = lokasi,
                    rating = rating,
                    komentar = comment
                )
                reviewViewModel.addReview(review, imageUri,
                    onSuccess = { showReviewDialog = false },
                    onError = { /* show snackbar or Toast */ }
                )
            }
        })
    }

    if (showCrowdDialog) {
        CrowdUpdateDialog(currentLevel = crowdMeter?.level ?: CrowdLevel.LOW, onDismiss = { showCrowdDialog = false }, onUpdate = { level ->
            val currentUser = authState
            if (currentUser is AuthState.Authenticated) {
                crowdMeterViewModel.updateCrowdLevel(lokasi = lokasi, level = level, userId = currentUser.user.id,
                    onSuccess = { showCrowdDialog = false },
                    onError = { /* show error */ }
                )
            }
        })
    }
}
package edu.unikom.culinarynight.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.unikom.culinarynight.data.model.AuthState
import edu.unikom.culinarynight.ui.screens.auth.LoginScreen
import edu.unikom.culinarynight.ui.screens.auth.RegisterScreen
import edu.unikom.culinarynight.ui.screens.main.HomeScreen
import edu.unikom.culinarynight.ui.screens.main.PKLDetailScreen
import edu.unikom.culinarynight.ui.screens.main.VoucherScreen
import edu.unikom.culinarynight.ui.screens.main.ProfileScreen
import edu.unikom.culinarynight.ui.screens.auth.LoadingScreen
import edu.unikom.culinarynight.viewmodel.AuthViewModel

@Composable
fun CulinaryNavigation(
    navController: NavHostController,
    authState: AuthState,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = when (authState) {
            is AuthState.Loading -> "loading"
            is AuthState.Authenticated -> "home"
            is AuthState.Unauthenticated -> "login"
            is AuthState.Error -> "login"
        }
    ) {
        // Auth screens
        composable("loading") {
            LoadingScreen()
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        // Main screens
        composable("home") {
            HomeScreen(
                onNavigateToPKLDetail = { pklData ->
                    navController.navigate("pkl_detail/${pklData.lokasi}")
                },
                onNavigateToVoucher = {
                    navController.navigate("voucher")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        composable("pkl_detail/{lokasi}") { backStackEntry ->
            val lokasi = backStackEntry.arguments?.getString("lokasi") ?: ""
            PKLDetailScreen(
                lokasi = lokasi,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("voucher") {
            VoucherScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
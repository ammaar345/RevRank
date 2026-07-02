package com.revrank

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.revrank.presentation.navigation.Screen
import com.revrank.presentation.screens.challenge.ChallengeAcceptanceScreen
import com.revrank.presentation.screens.onboarding.OnboardingScreen
import com.revrank.presentation.screens.onboarding.OnboardingViewModel
import com.revrank.presentation.screens.onboarding.PermissionsScreen
import com.revrank.presentation.screens.onboarding.SignInScreen
import com.revrank.presentation.screens.onboarding.UsernamePickerScreen
import com.revrank.presentation.screens.onboarding.VehicleTypeScreen
import com.revrank.presentation.screens.home.HomeScreen
import com.revrank.presentation.screens.leaderboard.LeaderboardScreen
import com.revrank.presentation.screens.paywall.PaywallScreen
import com.revrank.presentation.screens.paywall.ProActivatedScreen
import com.revrank.presentation.screens.profile.ProfileScreen
import com.revrank.presentation.screens.profile.PublicProfileScreen
import com.revrank.presentation.screens.ranks.RanksScreen
import com.revrank.presentation.screens.trip.LiveHudScreen
import com.revrank.presentation.screens.trips.TripsScreen
import com.revrank.presentation.screen.analytics.AnalyticsScreen
import com.revrank.presentation.screens.gforce.GForceScreen
import com.revrank.presentation.screens.routeReplay.RouteReplayScreen
import com.revrank.presentation.theme.RevRankTheme
import com.revrank.presentation.viewmodel.MainViewModel
import com.revrank.presentation.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tripViewModel: TripViewModel by viewModel()
    private val mainViewModel: MainViewModel by hiltViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Handle the intent that launched the activity
        handleIntent(intent)
        setContent {
            val navController = rememberNavController()
            RevRankApp(
                navController = navController,
                tripViewModel = tripViewModel,
                mainViewModel = mainViewModel
            )
        }
    }

    public override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null) {
            // Set the deep link in the viewModel
            mainViewModel.setPendingDeepLink(data)
        }
    }

    private fun handleDeepLink(uri: Uri, navController: NavHostController) {
        val path = uri.path
        when {
            path?.startsWith("/trip/") == true -> {
                val tripId = path.substringAfter("/trip/")
                // Navigate to RouteReplayScreen with tripId
                navController.navigate("route_replay/${tripId}") {
                    // Pop up to the start destination of the graph to avoid building up a large back stack
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    // Avoid multiple copies of the same destination when reselecting an item
                    launchSingleTop = true
                    // Restore state when navigating back to the destination
                    restoreState = true
                }
            }
            path?.startsWith("/challenge/") == true -> {
                val challengeId = path.substringAfter("/challenge/")
                // TODO: Navigate to ChallengeAcceptanceScreen with challengeId
                // For now, we'll just navigate to a placeholder
                navController.navigate("challenge_accept") {
                    argument("challengeId") { true }
                    argument("challengeId") { defaultValue = challengeId }
                }
            }
            path?.startsWith("/profile/") == true -> {
                val username = path.substringAfter("/profile/")
                // TODO: Navigate to PublicProfileScreen with username
                navController.navigate("public_profile") {
                    argument("username") { true }
                    argument("username") { defaultValue = username }
                }
            }
            path?.startsWith("/ref/") == true -> {
                val referralCode = path.substringAfter("/ref/")
                // TODO: Handle referral (maybe show a referral screen or apply credit)
                // For now, we'll just navigate to home and maybe show a toast
                // We'll need to process the referral in the ViewModel or repository.
                // Let's just navigate to home for now and handle the referral elsewhere.
                navController.navigate("home") {
                    // Pop up to the start destination to avoid stacking
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
                // TODO: Pass the referral code to the HomeViewModel to process
            }
            else -> {
                // If the deep link doesn't match any pattern, just go to home
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
        }
    }

    @Composable
    fun RevRankApp(
        navController: NavHostController,
        tripViewModel: TripViewModel,
        mainViewModel: MainViewModel
    ) {
        RevRankTheme {
            // Handle deep links
            val pendingUri by mainViewModel.pendingDeepLink.collectAsState()
            LaunchedEffect(pendingUri) {
                pendingUri?.let { uri ->
                    handleDeepLink(uri, navController)
                    // Clear after handling
                    mainViewModel.clearPendingDeepLink()
                }
            }

            // If onboarding not complete, show onboarding flow
            val onboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>()
            val onboardingComplete by onboardingViewModel.onboardingCollectAsState()
            val isTripActive by tripViewModel.isTripActive.collectAsState(initialValue = false)

            if (!onboardingComplete) {
                OnboardingNavHost(navController, onboardingViewModel)
            } else {
                // If a trip is active, show the Live HUD screen (full screen)
                if (isTripActive) {
                    LiveHudScreen(
                        onTripEnded = { /* Navigate back to main nav host */ }
                    )
                } else {
                    MainNavHost(navController)
                }
            }
        }
    }

    @Composable
    fun OnboardingNavHost(
        navController: NavHostController,
        viewModel: OnboardingViewModel
    ) {
        val tweenSpec = tween<Float>(300)
        androidx.navigation.compose.NavHost(navController = navController, startDestination = "onboarding") {
            composable(
                route = "onboarding",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                OnboardingScreen(
                    onComplete = { navController.navigate("signin") }
                )
            }
            composable(
                route = "signin",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                SignInScreen(
                    onGoogleSignIn = { /* Call AuthRepository */ },
                    onAppleSignIn = { /* Placeholder */ }
                )
            }
            composable(
                route = "username",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                UsernamePickerScreen(
                    onConfirm = { navController.navigate("vehicle") },
                    onCheckAvailability = { viewModel.checkUsername(it) },
                    isAvailable = viewModel.usernameAvailable.collectAsState().value
                )
            }
            composable(
                route = "vehicle",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                VehicleTypeScreen(
                    onConfirm = { navController.navigate("permissions") }
                )
            }
            composable(
                route = "permissions",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                PermissionsScreen(
                    onGrantLocation = { viewModel.setLocationGranted(true) },
                    onGrantMotion = { viewModel.setMotionGranted(true) },
                    locationGranted = viewModel.locationGranted.collectAsState().value,
                    motionGranted = viewModel.motionGranted.collectAsState().value,
                    onComplete = {
                        viewModel.completeOnboarding()
                        // Navigate to main app
                    }
                )
            }
        }
    }

    @Composable
    fun MainNavHost(navController: NavHostController) {
        val tweenSpec = tween<Float>(300)
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) }
            ) {
                val homeViewModel = androidx.lifecycle.viewmodel.compose.viewModel<HomeViewModel>()
                val uiState by homeViewModel.uiState.collectAsState()
                HomeScreen(
                    username = uiState.username,
                    xp = uiState.xp,
                    rank = uiState.rank,
                    streakDays = uiState.streakDays,
                    weeklyTrips = uiState.weeklyTrips,
                    weeklyKm = uiState.weeklyKm,
                    challenges = uiState.challenges,
                    lastTrip = uiState.lastTrip,
                    onStartTrip = { /* TODO: Implement start trip */ },
                    onViewRanks = { /* TODO: Navigate to RanksScreen */ },
                    onViewTrip = { tripId -> /* TODO: Navigate to trip detail */ }
                )
            }
            composable(
                route = Screen.Trips.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { TripsScreen() }
            composable(
                route = Screen.Ranks.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { RanksScreen() }
            composable(
                route = Screen.Profile.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { ProfileScreen() }
            composable(
                route = Screen.ChallengeAcceptance.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                val challengeId = it.arguments?.getString("challengeId") ?: ""
                ChallengeAcceptanceScreen(
                    challengeId = challengeId,
                    onChallengeAccepted = { /* TODO: Handle acceptance */ }
                )
            }
            composable(
                route = Screen.Leaderboard.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { LeaderboardScreen() }
            composable(
                route = Screen.PublicProfile.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                val username = it.arguments?.getString("username") ?: ""
                PublicProfileScreen(
                    username = username,
                    onDismiss = { /* TODO: Handle dismiss */ }
                )
            }
            // Pro feature screens
            composable(
                route = "route_replay/{tripId}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                val tripId = it.arguments?.getString("tripId") ?: ""
                RouteReplayScreen(
                    tripId = tripId,
                    onDismiss = { navController.popBackStack() },
                    onUpgradeClick = { navController.navigate(Screen.Paywall.route) }
                )
            }
            composable(
                route = "gforce/{tripId}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                val tripId = it.arguments?.getString("tripId") ?: ""
                GForceScreen(
                    tripId = tripId,
                    onDismiss = { navController.popBackStack() },
                    onUpgradeClick = { navController.navigate(Screen.Paywall.route) }
                )
            }
            composable(
                route = Screen.Analytics.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { AnalyticsScreen() }
            // Monetization screens
            composable(
                route = Screen.Paywall.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                PaywallScreen(
                    onDismiss = { navController.popBackStack() },
                    onProActivated = {
                        navController.navigate(Screen.ProActivated.route) {
                            popUpTo(Screen.Paywall.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.ProActivated.route,
                enterTransition = { fadeIn(tweenSpec) },
                exitTransition = { fadeOut(tweenSpec) }
            ) {
                ProActivatedScreen(
                    onAutoDismiss = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
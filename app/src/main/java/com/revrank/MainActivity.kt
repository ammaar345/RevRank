package com.revrank

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revrank.presentation.navigation.Screen
import com.revrank.presentation.screen.analytics.AnalyticsScreen
import com.revrank.presentation.screens.challenge.ChallengeAcceptanceScreen
import com.revrank.presentation.screens.gforce.GForceScreen
import com.revrank.presentation.screens.home.HomeScreen
import com.revrank.presentation.screens.leaderboard.LeaderboardScreen
import com.revrank.presentation.screens.onboarding.OnboardingScreen
import com.revrank.presentation.screens.onboarding.OnboardingViewModel
import com.revrank.presentation.screens.onboarding.PermissionsScreen
import com.revrank.presentation.screens.onboarding.SignInScreen
import com.revrank.presentation.screens.onboarding.UsernamePickerScreen
import com.revrank.presentation.screens.onboarding.VehicleTypeScreen
import com.revrank.presentation.screens.paywall.PaywallScreen
import com.revrank.presentation.screens.paywall.ProActivatedScreen
import com.revrank.presentation.screens.profile.ProfileScreen
import com.revrank.presentation.screens.profile.PublicProfileScreen
import com.revrank.presentation.screens.ranks.RanksScreen
import com.revrank.presentation.screens.trip.LiveHudScreen
import com.revrank.presentation.screens.trips.TripsScreen
import com.revrank.presentation.theme.RevRankTheme
import com.revrank.presentation.viewmodel.MainViewModel
import com.revrank.presentation.viewmodel.TripViewModel
import com.revrank.presentation.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val tripViewModel: TripViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Swap the splash/launch window background (which shows the icon) for the
        // solid-black app theme so the icon doesn't bleed through Compose content.
        setTheme(R.style.Theme_RevRank)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Handle the intent that launched the activity
        handleIntent(intent)
        setContent {
            RevRankApp(
                tripViewModel = tripViewModel,
                mainViewModel = mainViewModel
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { mainViewModel.setPendingDeepLink(it) }
    }

    private fun handleDeepLink(uri: Uri, navController: NavHostController) {
        val path = uri.path
        when {
            path?.startsWith("/trip/") == true -> {
                val tripId = path.substringAfter("/trip/")
                navController.navigate("route_replay/$tripId") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                }
            }
            path?.startsWith("/challenge/") == true -> {
                val challengeId = path.substringAfter("/challenge/")
                navController.navigate("challenge_acceptance/$challengeId") {
                    launchSingleTop = true
                }
            }
            path?.startsWith("/profile/") == true -> {
                val username = path.substringAfter("/profile/")
                navController.navigate("public_profile/$username") {
                    launchSingleTop = true
                }
            }
            path?.startsWith("/ref/") == true -> {
                // TODO: apply referral code via UserRepository.applyReferralBonus once auth lands
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
            else -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
        }
    }

    @Composable
    fun RevRankApp(
        tripViewModel: TripViewModel,
        mainViewModel: MainViewModel
    ) {
        RevRankTheme {
            val navController = rememberNavController()

            // Handle deep links
            val pendingUri by mainViewModel.pendingDeepLink.collectAsState()
            LaunchedEffect(pendingUri) {
                pendingUri?.let { uri ->
                    handleDeepLink(uri, navController)
                    mainViewModel.clearPendingDeepLink()
                }
            }

            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            val onboardingComplete by onboardingViewModel.onboardingComplete.collectAsState()
            val isTripActive by tripViewModel.isTripActive.collectAsState()
            val justEndedTrip by tripViewModel.justEndedTrip.collectAsState()

            when {
                !onboardingComplete -> OnboardingNavHost(navController, onboardingViewModel)
                isTripActive -> LiveHudScreen(
                    onTripEnded = { tripViewModel.captureEndingTrip() }
                )
                justEndedTrip != null -> {
                    val ended = justEndedTrip!!
                    val tripEndViewModel: com.revrank.presentation.viewmodel.TripEndViewModel = hiltViewModel()
                    val user = androidx.compose.runtime.remember(ended.id) {
                        com.revrank.domain.model.User(
                            uid = ended.userId,
                            username = "You",
                            displayName = "You",
                            lastTripDate = ended.endTime
                        )
                    }
                    // Award XP / evaluate badges / rank-up for the finalized trip.
                    LaunchedEffect(ended.id) { tripEndViewModel.onTripEnded(ended, user) }
                    val xpGained by tripEndViewModel.xpGained.collectAsState()
                    val newBadges by tripEndViewModel.newBadges.collectAsState()
                    com.revrank.presentation.screens.score.TripEndScreen(
                        trip = ended,
                        user = user,
                        newBadges = newBadges,
                        xpGained = xpGained,
                        onViewHistory = {
                            tripEndViewModel.dismissReward()
                            tripViewModel.clearEndedTrip()
                        },
                        onBadgeDismissed = { tripViewModel.clearEndedTrip() }
                    )
                }
                else -> MainNavHost(navController)
            }
        }
    }

    @Composable
    fun OnboardingNavHost(
        navController: NavHostController,
        viewModel: OnboardingViewModel
    ) {
        val tweenSpec = tween<androidx.compose.ui.unit.IntOffset>(300)
        NavHost(navController = navController, startDestination = Screen.Onboarding.route) {
            composable(
                route = Screen.Onboarding.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                OnboardingScreen(onComplete = { navController.navigate(Screen.SignIn.route) })
            }
            composable(
                route = Screen.SignIn.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                SignInScreen(
                    onAuthenticated = { navController.navigate(Screen.UsernamePicker.route) }
                )
            }
            composable(
                route = Screen.UsernamePicker.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                UsernamePickerScreen(
                    onConfirm = { navController.navigate(Screen.VehicleType.route) },
                    onCheckAvailability = { viewModel.checkUsername(it) },
                    isAvailable = viewModel.usernameAvailable.collectAsState().value
                )
            }
            composable(
                route = Screen.VehicleType.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                VehicleTypeScreen(
                    onConfirm = { type ->
                        viewModel.setVehicleType(type)
                        navController.navigate(Screen.Permissions.route)
                    }
                )
            }
            composable(
                route = Screen.Permissions.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                PermissionsScreen(
                    onGrantLocation = { viewModel.setLocationGranted(true) },
                    onGrantMotion = { viewModel.setMotionGranted(true) },
                    locationGranted = viewModel.locationGranted.collectAsState().value,
                    motionGranted = viewModel.motionGranted.collectAsState().value,
                    onComplete = { viewModel.completeOnboarding() }
                )
            }
        }
    }

    @Composable
    fun MainNavHost(navController: NavHostController) {
        val tweenSpec = tween<androidx.compose.ui.unit.IntOffset>(300)
        val fadeSpec = tween<Float>(300)
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                com.revrank.presentation.navigation.MainShell(
                    onOpenTrip = { tripId -> navController.navigate("route_replay/$tripId") },
                    onUpgrade = { navController.navigate(Screen.Paywall.route) },
                    onOpenLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                )
            }
            composable(
                route = Screen.Trips.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                TripsScreen(
                    onTripClick = { tripId -> navController.navigate("route_replay/$tripId") },
                    onUpgradeClick = { navController.navigate(Screen.Paywall.route) }
                )
            }
            composable(
                route = Screen.Ranks.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val uiState by homeViewModel.uiState.collectAsState()
                RanksScreen(
                    currentRank = uiState.rank,
                    xp = uiState.xp,
                    badges = emptyList() // TODO: badge persistence
                )
            }
            composable(
                route = Screen.Profile.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { ProfileScreen() }
            composable(
                route = "challenge_acceptance/{challengeId}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { backStackEntry ->
                val challengeId = backStackEntry.arguments?.getString("challengeId") ?: ""
                ChallengeAcceptanceScreen(
                    challengeId = challengeId,
                    onChallengeAccepted = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Leaderboard.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) {
                LeaderboardScreen(
                    onBack = { navController.popBackStack() },
                    onUpgradeToPro = { navController.navigate(Screen.Paywall.route) }
                )
            }
            composable(
                route = "public_profile/{username}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                PublicProfileScreen(
                    username = username,
                    onBack = { navController.popBackStack() }
                )
            }
            // Pro feature screens
            composable(
                route = "route_replay/{tripId}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                com.revrank.presentation.screens.routeReplay.RouteReplayScreen(
                    tripId = tripId,
                    onDismiss = { navController.popBackStack() },
                    onUpgradeClick = { navController.navigate(Screen.Paywall.route) }
                )
            }
            composable(
                route = "gforce/{tripId}",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tweenSpec) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tweenSpec) }
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
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
            ) {
                AnalyticsScreen(onBack = { navController.popBackStack() })
            }
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
                enterTransition = { fadeIn(fadeSpec) },
                exitTransition = { fadeOut(fadeSpec) }
            ) {
                ProActivatedScreen(onAutoDismiss = { navController.popBackStack() })
            }
        }
    }
}

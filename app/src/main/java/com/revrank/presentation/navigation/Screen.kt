package com.revrank.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object SignIn : Screen("signin")
    object UsernamePicker : Screen("username")
    object VehicleType : Screen("vehicle")
    object Permissions : Screen("permissions")
    object Home : Screen("home")
    object Trips : Screen("trips")
    object Ranks : Screen("ranks")
    object Profile : Screen("profile")
    object PublicProfile : Screen("public_profile")
    object LiveHud : Screen("livehud")
    object TripEnd : Screen("trip_end")
    object RankUp : Screen("rank_up")
    object BadgeEarned : Screen("badge_earned")
    object ShareCardPreview : Screen("share_preview")
    object Settings : Screen("settings")
    object ChallengeAcceptance : Screen("challenge_acceptance")
    object Leaderboard : Screen("leaderboard")
    // Pro features
    object RouteReplay : Screen("route_replay") {
        // This screen expects a tripId argument
    }
    object GForce : Screen("gforce") {
        // This screen expects a tripId argument
    }
    object Analytics : Screen("analytics")
    // Monetization
    object Paywall : Screen("paywall")
    object ProActivated : Screen("pro_activated")
}
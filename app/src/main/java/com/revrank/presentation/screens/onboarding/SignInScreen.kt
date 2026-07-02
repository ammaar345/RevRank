package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.RevRankTypography
import com.revrank.presentation.theme.Void

@Composable
fun SignInScreen(
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo placeholder
        Text(
            text = "RevRank",
            style = RevRankTypography.headlineLarge,
            color = MatrixGreen
        )
        Spacer(modifier = Modifier.height(48.dp))
        RevRankButton(
            text = "Sign in with Google",
            onClick = onGoogleSignIn
        )
        Spacer(modifier = Modifier.height(16.dp))
        RevRankButton(
            text = "Sign in with Apple",
            onClick = onAppleSignIn
        )
    }
}

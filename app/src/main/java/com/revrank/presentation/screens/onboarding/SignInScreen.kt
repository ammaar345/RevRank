package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.theme.JetBrainsMono
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.RevRankTypography
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.Void

@Composable
fun SignInScreen(
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Terminal chrome tag
        Text(
            text = "> REVRANK // SIGN_IN",
            style = RevRankTypography.labelSmall,
            color = Phosphor.copy(alpha = 0.55f)
        )
        Spacer(modifier = Modifier.height(40.dp))

        // Logo — Orbitron display with phosphor glow
        Text(
            text = "REVRANK",
            style = RevRankTypography.displayMedium,
            color = Phosphor
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "YOUR RIDE HAS A SCORE",
            style = RevRankTypography.labelMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(56.dp))

        RevRankButton(
            text = "Sign in with Google",
            onClick = onGoogleSignIn,
            primary = true
        )
        Spacer(modifier = Modifier.height(14.dp))
        RevRankButton(
            text = "Sign in with Apple",
            onClick = onAppleSignIn
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "By continuing you agree to the Terms & Privacy Policy",
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

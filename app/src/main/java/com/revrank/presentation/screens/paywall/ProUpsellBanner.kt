package com.revrank.presentation.screens.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.*

/**
 * Inline soft gate shown inside ProGate fallback.
 * Shows dimmed/blurred feature preview with a lock overlay and subscribe CTA.
 *
 * @param featureName Short name of the feature (e.g., "Route Replay")
 * @param featureDescription Context-specific copy about what the feature does
 * @param onSubscribe Called when user taps subscribe CTA — navigates to PaywallScreen
 */
@Composable
fun ProUpsellBanner(
    featureName: String = "Pro feature",
    featureDescription: String = "Unlock this and all Pro features with a subscription.",
    onDismiss: () -> Unit = {},
    onSubscribe: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Blurred/dimmed feature preview background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Surface2, RoundedCornerShape(14.dp))
                .alpha(0.5f)
        )

        // Foreground lock overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Surface2.copy(alpha = 0.85f),
                    RoundedCornerShape(14.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lock icon
            Text(
                text = "🔒",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature name
            Text(
                text = featureName,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Feature description
            Text(
                text = featureDescription,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CTA
            Button(
                onClick = onSubscribe,
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreen,
                    contentColor = Void
                )
            ) {
                Text(
                    text = "UNLOCK PRO",
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dim dismiss text
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Not now",
                    color = TextDim,
                    fontSize = 11.sp
                )
            }
        }
    }
}

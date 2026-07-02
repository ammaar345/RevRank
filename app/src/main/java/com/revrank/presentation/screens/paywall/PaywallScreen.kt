package com.revrank.presentation.screens.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.data.revenuecat.PurchaseResult
import com.revrank.domain.model.PricingPeriod
import com.revrank.presentation.theme.*

private val features = listOf(
    "Unlimited trip history",
    "Route replay with score overlay",
    "G-force visualizer",
    "Advanced analytics & trends",
    "Friends leaderboard",
    "Home screen widget",
    "Unlimited streak freezes",
    "CSV data export"
)

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    onProActivated: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle purchase result
    LaunchedEffect(state.purchaseResult) {
        when (val result = state.purchaseResult) {
            is PurchaseResult.Success -> {
                onProActivated()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
    ) {
        // Background HUD glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MatrixGlow.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- Dismiss button ---
            Box(modifier = Modifier.fillMaxWidth().padding(end = 8.dp)) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Hero section ---
            if (state.isLoading) {
                LoadingSkeleton()
            } else if (state.error != null && state.offerings == null) {
                ErrorState(
                    message = state.error,
                    onRetry = { viewModel.retry() },
                    onDismiss = onDismiss
                )
            } else {
                HeroSection()

                Spacer(modifier = Modifier.height(24.dp))

                // --- Feature list ---
                FeatureList()

                Spacer(modifier = Modifier.height(24.dp))

                // --- Pricing toggle ---
                val pricing = viewModel.getPricingStrings()
                if (pricing != null) {
                    PricingToggle(
                        selectedPeriod = state.selectedPeriod,
                        onPeriodSelected = { viewModel.selectPeriod(it) },
                        pricing = pricing
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Free trial banner (first encounter only) ---
                    AnimatedVisibility(
                        visible = state.isFirstEncounter,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        TrialBanner()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- CTA ---
                    CtaButton(
                        label = if (state.isFirstEncounter) "START FREE TRIAL" else "UPGRADE TO PRO",
                        enabled = !state.isPurchasing,
                        isLoading = state.isPurchasing,
                        onClick = { viewModel.purchase(context as Activity) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // --- Restore link ---
                    RestoreLink(
                        enabled = !state.isPurchasing,
                        onClick = { viewModel.restore() }
                    )

                    // --- Error message ---
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = ScorePoor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "UNLOCK YOUR FULL",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "POTENTIAL",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Animated "PRO" badge
        Text(
            text = "PRO",
            style = MaterialTheme.typography.displaySmall,
            color = MatrixGreen,
            fontWeight = FontWeight.Bold,
            letterSpacing = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MatrixMuted.copy(alpha = 0.3f))
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun FeatureList() {
    Column(
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        features.forEachIndexed { index, feature ->
            val visible by remember { mutableStateOf(true) }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    // Diamond icon
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val diamond = Path().apply {
                            moveTo(size.width / 2, 0f)
                            lineTo(size.width, size.height / 2)
                            lineTo(size.width / 2, size.height)
                            lineTo(0f, size.height / 2)
                            close()
                        }
                        drawPath(diamond, color = MatrixGreen)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = feature,
                        color = TextPrimary.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PricingToggle(
    selectedPeriod: PricingPeriod,
    onPeriodSelected: (PricingPeriod) -> Unit,
    pricing: PricingStrings
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        // Segmented control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface2),
            horizontalArrangement = Arrangement.Center
        ) {
            TabItem(
                label = "Monthly",
                price = pricing.monthlyLabel,
                isSelected = selectedPeriod == PricingPeriod.MONTHLY,
                onClick = { onPeriodSelected(PricingPeriod.MONTHLY) },
                modifier = Modifier.weight(1f)
            )
            TabItem(
                label = "Annual",
                price = pricing.annualLabel,
                isSelected = selectedPeriod == PricingPeriod.ANNUAL,
                badge = if (pricing.annualSavings > 0) "SAVE ${pricing.annualSavings}%" else null,
                onClick = { onPeriodSelected(PricingPeriod.ANNUAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MatrixGreen.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = if (isSelected) MatrixGreen else TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = price,
                    color = if (isSelected) MatrixGreen else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                badge?.let {
                    Text(
                        text = "  $it",
                        color = Warning,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TrialBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(MatrixMuted.copy(alpha = 0.4f))
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            color = MatrixGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "7-day free trial included — cancel anytime",
            color = MatrixGreen,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CtaButton(
    label: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(56.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MatrixGreen,
            contentColor = Void,
            disabledContainerColor = MatrixGreen.copy(alpha = 0.4f),
            disabledContentColor = Void.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Void,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = label,
                fontSize = 14.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RestoreLink(
    enabled: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Restore purchases",
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun LoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        CircularProgressIndicator(
            color = MatrixGreen,
            modifier = Modifier.size(40.dp),
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading...",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ErrorState(
    message: String?,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = message ?: "Something went wrong",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixGreen)
        ) {
            Text("RETRY")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onDismiss) {
            Text("CANCEL", color = TextSecondary)
        }
    }
}

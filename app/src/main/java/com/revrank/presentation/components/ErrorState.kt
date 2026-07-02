package com.revrank.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.TextDim

sealed class ErrorDisplay(val friendlyMessage: String) {
    data object Network : ErrorDisplay("Couldn't connect. Check your signal.")
    data object Auth : ErrorDisplay("Session expired. Sign in again.")
    data object Generic : ErrorDisplay("Something went wrong. Try again.")
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextDim,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        RevRankButton(
            text = "RETRY",
            onClick = onRetry
        )
    }
}

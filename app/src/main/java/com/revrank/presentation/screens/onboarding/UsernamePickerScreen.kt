package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.theme.Surface
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.RevRankTypography
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun UsernamePickerScreen(
    onConfirm: (String) -> Unit,
    onCheckAvailability: (String) -> Unit,
    isAvailable: Boolean?
) {
    var username by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PICK YOUR DRIVER NAME",
            style = RevRankTypography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isLetterOrDigit() || it == '_' }
                if (filtered.length <= 16) {
                    username = filtered
                    if (filtered.length >= 3) {
                        onCheckAvailability(filtered)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username", style = RevRankTypography.labelSmall) },
            textStyle = RevRankTypography.bodyLarge.copy(fontFamily = ShareTechMono),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            isError = isAvailable == false
        )
        Spacer(modifier = Modifier.height(8.dp))
        when (isAvailable) {
            true -> Text("Available", color = MatrixGreen, style = RevRankTypography.bodyMedium)
            false -> Text("Taken", color = com.revrank.presentation.theme.Danger, style = RevRankTypography.bodyMedium)
            else -> Text("", style = RevRankTypography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Suggestions:",
            style = RevRankTypography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        val suggestions = remember {
            val adjectives = listOf("Fast", "Smooth", "Cool", "Wild", "Quiet", "Swift")
            val nouns = listOf("Rider", "Ghost", "Pilot", "Driver", "Racer", "Wolf")
            adjectives.zip(nouns).map { (adj, noun) ->
                "${adj}_${noun}_${(10..99).random()}"
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            suggestions.forEach { suggestion ->
                Text(
                    text = suggestion,
                    color = TextSecondary,
                    style = RevRankTypography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        RevRankButton(
            text = "Confirm",
            onClick = { onConfirm(username) },
            enabled = username.length >= 3 && isAvailable == true
        )
    }
}

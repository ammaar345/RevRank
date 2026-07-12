package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.components.TerminalScaffold
import com.revrank.presentation.theme.Border
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.RevRankTypography
import com.revrank.domain.model.VehicleType

@Composable
fun VehicleTypeScreen(
    onConfirm: (VehicleType) -> Unit
) {
    val selected = remember { mutableStateOf<VehicleType?>(null) }
    val options = listOf(
        VehicleType.CAR to "Car",
        VehicleType.MOTORCYCLE to "Motorcycle",
        VehicleType.VAN to "Van",
        VehicleType.TRUCK to "Truck"
    )

    TerminalScaffold(screenId = "VEHICLE") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "WHAT DO YOU DRIVE?",
                style = RevRankTypography.headlineLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                options.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowOptions.forEach { (type, label) ->
                            val isSelected = selected.value == type
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MatrixGreen else Border,
                                        shape = RectangleShape
                                    )
                                    .clickable { selected.value = type }
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = label,
                                    style = RevRankTypography.bodyLarge,
                                    color = if (isSelected) MatrixGreen else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            RevRankButton(
                text = "Confirm",
                onClick = { selected.value?.let { onConfirm(it) } },
                enabled = selected.value != null,
                primary = true
            )
        }
    }
}

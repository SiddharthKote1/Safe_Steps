package com.Siddharth.SafeSteps.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepProgressIndicator(currentStep: Int, totalSteps: Int = 3) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary 
                        else Color.LightGray.copy(alpha = 0.4f)
                    )
            )
            if (i < totalSteps) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun getPremiumBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun PremiumCardModifier(): Modifier {
    return Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp)
        )
}

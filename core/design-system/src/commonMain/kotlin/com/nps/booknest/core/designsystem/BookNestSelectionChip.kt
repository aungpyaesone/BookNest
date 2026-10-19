package com.nps.booknest.core.designsystem

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BookNestSelectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Unspecified,
    textStyle: TextStyle = bookNestCompactTypography.labelSmall,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                label,
                modifier = if (textAlign == TextAlign.Center) Modifier.width(IntrinsicSize.Max) else Modifier,
                style = textStyle,
                textAlign = textAlign,
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors =
            FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
    )
}

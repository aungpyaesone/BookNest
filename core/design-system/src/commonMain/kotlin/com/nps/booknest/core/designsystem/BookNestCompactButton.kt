package com.nps.booknest.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun BookNestCompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = bookNestCompactTypography.labelSmall,
) {
    Box(
        modifier.widthIn(min = 48.dp).heightIn(min = 48.dp).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.heightIn(min = 32.dp).testTag("booknest_compact_button_visual"),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Text(
                text,
                Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                style = textStyle,
            )
        }
    }
}

package com.nps.booknest.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BookNestLogo(
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = BookNestSize.logoIcon,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = bookNestLogoIcon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "BookNest",
            color = MaterialTheme.colorScheme.onBackground,
            style = textStyle,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun BookNestPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 40.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(horizontal = BookNestSpacing.medium),
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null)
        }
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = BookNestSpacing.extraSmall),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun BookNestOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 40.dp),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(horizontal = BookNestSpacing.medium),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}

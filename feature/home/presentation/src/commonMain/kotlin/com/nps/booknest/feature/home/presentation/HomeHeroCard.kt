package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_hero_books
import com.nps.booknest.feature.home.presentation.generated.resources.home_hero_image_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HeroCard(
    banner: HomeBannerUi,
    page: Int,
    onExploreClick: () -> Unit,
    layoutMode: HomeLayoutMode,
) {
    val layout = heroCardLayout(layoutMode)
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = layout.minimumHeight)
                .testTag(HomeTestTags.HERO_CARD),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = heroCardColor(page)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        HeroCardContent(
            banner = banner,
            onExploreClick = onExploreClick,
            layoutMode = layoutMode,
            layout = layout,
        )
    }
}

@Composable
private fun HeroCardContent(
    banner: HomeBannerUi,
    onExploreClick: () -> Unit,
    layoutMode: HomeLayoutMode,
    layout: HeroCardLayout,
) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = layout.minimumHeight)) {
        HeroCopy(
            banner = banner,
            onExploreClick = onExploreClick,
            layoutMode = layoutMode,
            layout = layout,
            modifier = Modifier.weight(layout.copyWeight),
        )
        AsyncImage(
            model = banner.imageUrl,
            placeholder = painterResource(Res.drawable.home_hero_books),
            error = painterResource(Res.drawable.home_hero_books),
            contentDescription = stringResource(Res.string.home_hero_image_description),
            modifier =
                Modifier
                    .weight(layout.artworkWeight)
                    .height(layout.minimumHeight)
                    .padding(top = BookNestSpacing.medium),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterEnd,
        )
    }
}

@Composable
private fun HeroCopy(
    banner: HomeBannerUi,
    onExploreClick: () -> Unit,
    layoutMode: HomeLayoutMode,
    layout: HeroCardLayout,
    modifier: Modifier = Modifier,
) {
    val isCompact = layoutMode == HomeLayoutMode.COMPACT
    val titleStyle =
        when (layoutMode) {
            HomeLayoutMode.COMPACT -> MaterialTheme.typography.titleMedium
            HomeLayoutMode.MEDIUM -> MaterialTheme.typography.titleLarge
            HomeLayoutMode.EXPANDED -> MaterialTheme.typography.headlineSmall
        }
    val bodyStyle =
        if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    Column(
        modifier = modifier.padding(layout.contentPadding),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(banner.title, style = titleStyle, fontWeight = FontWeight.Bold)
        Text(
            text = banner.subtitle,
            modifier = Modifier.padding(top = BookNestSpacing.medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = bodyStyle,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
        BookNestPrimaryButton(
            text = banner.buttonText,
            onClick = onExploreClick,
            modifier =
                Modifier
                    .padding(top = if (isCompact) BookNestSpacing.medium else BookNestSpacing.large)
                    .testTag(HomeTestTags.HERO_ACTION),
        )
    }
}

@Composable
private fun heroCardColor(page: Int) =
    when (page) {
        1 -> MaterialTheme.colorScheme.secondaryContainer
        2 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

private fun heroCardLayout(layoutMode: HomeLayoutMode): HeroCardLayout =
    when (layoutMode) {
        HomeLayoutMode.COMPACT -> HeroCardLayout(224.dp, BookNestSpacing.large, 1.55f, 0.65f)
        HomeLayoutMode.MEDIUM -> HeroCardLayout(264.dp, BookNestSpacing.extraLarge, 1.3f, 0.8f)
        HomeLayoutMode.EXPANDED -> HeroCardLayout(288.dp, 24.dp, 1.15f, 1f)
    }

private data class HeroCardLayout(
    val minimumHeight: Dp,
    val contentPadding: Dp,
    val copyWeight: Float,
    val artworkWeight: Float,
)

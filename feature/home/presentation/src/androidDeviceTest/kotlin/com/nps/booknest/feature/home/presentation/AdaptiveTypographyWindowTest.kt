@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import com.nps.booknest.core.designsystem.BookNestAdaptiveText
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.core.designsystem.bookNestCompactTypography
import com.nps.booknest.core.designsystem.bookNestTabletTypography
import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveTypographyWindowTest {
    @Test
    fun windowThresholdAndSystemFontScalePreserveCompactAndReaderDefaults() {
        listOf(320, 360, 412, 599, 600, 820, 840, 1024, 1180).forEach { width ->
            listOf(1f, 1.3f, 1.5f).forEach { scale ->
                runComposeUiTest {
                    var original: Typography? = null
                    var adapted: Typography? = null
                    var compact: Typography? = null
                    var outside: Typography? = null
                    var actualScale = 0f
                    setContent {
                        val density =
                            LocalWindowInfo.current.containerSize.width
                                .toFloat() / width
                        CompositionLocalProvider(LocalDensity provides Density(density, scale)) {
                            BookNestTheme {
                                val before = MaterialTheme.typography
                                BookNestAdaptiveText {
                                    val enlarged = MaterialTheme.typography
                                    val preset = bookNestAdaptiveCompactTypography()
                                    val fontScale = LocalDensity.current.fontScale
                                    SideEffect {
                                        original = before
                                        adapted = enlarged
                                        compact = preset
                                        actualScale = fontScale
                                    }
                                }
                                val after = MaterialTheme.typography
                                SideEffect { outside = after }
                            }
                        }
                    }
                    waitForIdle()
                    assertEquals(original, outside)
                    assertEquals(scale, actualScale, 0.001f)
                    assertEquals(if (width >= 600) bookNestTabletTypography else bookNestCompactTypography, compact)
                    assertEquals(
                        original!!.bodyMedium.fontSize.value * if (width >= 600) 1.2f else 1f,
                        adapted!!.bodyMedium.fontSize.value,
                        0.001f,
                    )
                }
            }
        }
    }
}

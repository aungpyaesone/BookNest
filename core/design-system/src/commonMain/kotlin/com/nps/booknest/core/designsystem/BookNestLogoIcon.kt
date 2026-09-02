package com.nps.booknest.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val bookNestLogoIcon: ImageVector =
    ImageVector
        .Builder(
            name = "BookNestLogo",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            logoPath {
                moveTo(4f, 6.75f)
                curveTo(7.85f, 5.86f, 11.86f, 6.65f, 15.25f, 9.23f)
                verticalLineTo(25.63f)
                curveTo(11.83f, 23.56f, 7.96f, 22.94f, 4f, 23.75f)
                verticalLineTo(6.75f)
                close()
            }
            logoPath {
                moveTo(28f, 6.75f)
                curveTo(24.15f, 5.86f, 20.14f, 6.65f, 16.75f, 9.23f)
                verticalLineTo(25.63f)
                curveTo(20.17f, 23.56f, 24.04f, 22.94f, 28f, 23.75f)
                verticalLineTo(6.75f)
                close()
            }
            logoPath {
                moveTo(15.25f, 9.23f)
                curveTo(15.52f, 9.43f, 15.77f, 9.65f, 16f, 9.88f)
                curveTo(16.23f, 9.65f, 16.48f, 9.43f, 16.75f, 9.23f)
            }
            logoPath {
                moveTo(2.75f, 9.25f)
                horizontalLineTo(4f)
                moveTo(28f, 9.25f)
                horizontalLineTo(29.25f)
                verticalLineTo(26.1f)
                curveTo(24.92f, 25.2f, 20.42f, 26.02f, 16.75f, 28.35f)
                horizontalLineTo(15.25f)
                curveTo(11.58f, 26.02f, 7.08f, 25.2f, 2.75f, 26.1f)
                verticalLineTo(9.25f)
                close()
            }
        }.build()

private fun ImageVector.Builder.logoPath(pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit) {
    path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBuilder,
    )
}

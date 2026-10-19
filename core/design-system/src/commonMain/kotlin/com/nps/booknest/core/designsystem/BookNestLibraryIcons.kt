package com.nps.booknest.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val bookNestBookshelfIcon =
    libraryOutlineIcon("Bookshelf") {
        moveTo(2f, 18f)
        horizontalLineTo(22f)
        moveTo(4f, 18f)
        verticalLineTo(22f)
        moveTo(20f, 18f)
        verticalLineTo(22f)
        moveTo(4f, 4f)
        horizontalLineTo(7f)
        verticalLineTo(17f)
        horizontalLineTo(4f)
        close()
        moveTo(8f, 3f)
        horizontalLineTo(11f)
        verticalLineTo(17f)
        horizontalLineTo(8f)
        close()
        moveTo(12f, 6f)
        lineTo(15f, 5f)
        lineTo(19f, 16f)
        lineTo(16f, 17f)
        close()
        moveTo(4f, 7f)
        horizontalLineTo(7f)
        moveTo(8f, 6f)
        horizontalLineTo(11f)
        moveTo(14f, 10f)
        lineTo(17f, 9f)
    }

val bookNestOpenBookIcon =
    libraryOutlineIcon("OpenBook") {
        moveTo(12f, 5f)
        curveTo(9f, 3f, 5f, 3f, 2f, 4f)
        verticalLineTo(19f)
        curveTo(6f, 18f, 9f, 19f, 12f, 21f)
        curveTo(15f, 19f, 18f, 18f, 22f, 19f)
        verticalLineTo(4f)
        curveTo(19f, 3f, 15f, 3f, 12f, 5f)
        verticalLineTo(21f)
        moveTo(5f, 6f)
        verticalLineTo(16f)
        moveTo(19f, 6f)
        verticalLineTo(16f)
    }

val bookNestBookmarkStarIcon =
    libraryOutlineIcon("BookmarkStar") {
        moveTo(13f, 3f)
        horizontalLineTo(5f)
        verticalLineTo(21f)
        lineTo(10f, 17f)
        lineTo(15f, 21f)
        verticalLineTo(15f)
        moveTo(17f, 4f)
        lineTo(18.5f, 7.5f)
        lineTo(22f, 8f)
        lineTo(19.5f, 10.5f)
        lineTo(20f, 14f)
        lineTo(17f, 12.5f)
        lineTo(14f, 14f)
        lineTo(14.5f, 10.5f)
        lineTo(12f, 8f)
        lineTo(15.5f, 7.5f)
        close()
    }

val bookNestCircledCheckIcon =
    libraryOutlineIcon("CircledCheck") {
        moveTo(22f, 12f)
        curveTo(22f, 17.5f, 17.5f, 22f, 12f, 22f)
        curveTo(6.5f, 22f, 2f, 17.5f, 2f, 12f)
        curveTo(2f, 6.5f, 6.5f, 2f, 12f, 2f)
        curveTo(17.5f, 2f, 22f, 6.5f, 22f, 12f)
        close()
        moveTo(7f, 12f)
        lineTo(10.5f, 15.5f)
        lineTo(17f, 8.5f)
    }

val bookNestDownloadTrayIcon =
    libraryOutlineIcon("DownloadTray") {
        moveTo(12f, 2f)
        verticalLineTo(15f)
        moveTo(8f, 11f)
        lineTo(12f, 15f)
        lineTo(16f, 11f)
        moveTo(3f, 16f)
        verticalLineTo(21f)
        horizontalLineTo(21f)
        verticalLineTo(16f)
    }

val bookNestFunnelIcon =
    libraryOutlineIcon("Funnel") {
        moveTo(3f, 4f)
        horizontalLineTo(21f)
        lineTo(14f, 12f)
        verticalLineTo(21f)
        lineTo(10f, 18f)
        verticalLineTo(12f)
        close()
    }

private fun libraryOutlineIcon(
    name: String,
    outline: PathBuilder.() -> Unit,
): ImageVector =
    ImageVector
        .Builder(name, 24.dp, 24.dp, 24f, 24f)
        .apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathBuilder = outline,
            )
        }.build()

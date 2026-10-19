package com.nps.booknest.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.SportsMartialArts
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

// Backend icon keys and sample category keys share the same visual vocabulary.
@Suppress("CyclomaticComplexMethod")
fun bookNestCategoryIcon(iconKey: String): ImageVector =
    when (iconKey) {
        "all" -> Icons.Outlined.GridView
        "graduation-cap" -> Icons.Outlined.School
        "briefcase" -> Icons.Outlined.BusinessCenter
        "sprout" -> Icons.Outlined.VolunteerActivism
        "person" -> Icons.Outlined.PersonOutline
        "history" -> Icons.Outlined.HistoryEdu
        "science" -> Icons.Outlined.Science
        "technology" -> Icons.Outlined.DesktopWindows
        "art" -> Icons.Outlined.Palette
        "comics" -> Icons.AutoMirrored.Outlined.MenuBook
        "travel" -> Icons.Outlined.Map
        "religion" -> Icons.Outlined.SelfImprovement
        "children" -> Icons.Outlined.ChildCare
        "martial" -> Icons.Outlined.SportsMartialArts
        "more" -> Icons.Outlined.MoreHoriz
        else -> Icons.Outlined.AutoStories
    }

package com.nps.booknest.core.presentation

import androidx.compose.runtime.Composable
import com.nps.booknest.core.domain.DataError
import com.nps.booknest.core.presentation.generated.resources.Res
import com.nps.booknest.core.presentation.generated.resources.error_database_disk_full
import com.nps.booknest.core.presentation.generated.resources.error_database_not_found
import com.nps.booknest.core.presentation.generated.resources.error_database_unknown
import com.nps.booknest.core.presentation.generated.resources.error_network_unknown
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class DynamicString(
        val value: String,
    ) : UiText

    data class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText
}

@Composable
@Suppress("SpreadOperator") // Compose Resources exposes formatting arguments only through its vararg API.
fun UiText.asString(): String =
    when (this) {
        is UiText.DynamicString -> value
        is UiText.Resource -> stringResource(id, *args.toTypedArray())
    }

fun DataError.toUiText(): UiText =
    when (this) {
        DataError.Local.DISK_FULL -> UiText.Resource(Res.string.error_database_disk_full)
        DataError.Local.NOT_FOUND -> UiText.Resource(Res.string.error_database_not_found)
        DataError.Local.UNKNOWN -> UiText.Resource(Res.string.error_database_unknown)
        is DataError.Network -> UiText.Resource(Res.string.error_network_unknown)
    }

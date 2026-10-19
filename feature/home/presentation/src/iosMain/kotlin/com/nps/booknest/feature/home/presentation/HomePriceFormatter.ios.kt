package com.nps.booknest.feature.home.presentation

import com.nps.booknest.core.presentation.formatBookPrice

internal actual fun formatHomePrice(
    amount: Double,
    currencyCode: String,
): String = formatBookPrice(amount, currencyCode)

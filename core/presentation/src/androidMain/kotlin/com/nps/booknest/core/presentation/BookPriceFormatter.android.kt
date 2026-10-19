package com.nps.booknest.core.presentation

import java.text.NumberFormat
import java.util.Currency

actual fun formatBookPrice(
    amount: Double,
    currencyCode: String,
): String =
    try {
        val currency = Currency.getInstance(currencyCode)
        NumberFormat
            .getCurrencyInstance()
            .apply {
                this.currency = currency
                minimumFractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
                maximumFractionDigits = minimumFractionDigits
            }.format(amount)
    } catch (_: IllegalArgumentException) {
        "$currencyCode $amount"
    }

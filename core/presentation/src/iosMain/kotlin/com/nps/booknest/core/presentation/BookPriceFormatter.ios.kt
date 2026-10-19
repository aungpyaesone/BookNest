package com.nps.booknest.core.presentation

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun formatBookPrice(
    amount: Double,
    currencyCode: String,
): String =
    NSNumberFormatter()
        .apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            this.currencyCode = currencyCode
        }.stringFromNumber(NSNumber(double = amount)) ?: "$currencyCode $amount"

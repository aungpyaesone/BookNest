package com.nps.booknest.core.presentation

expect fun formatBookPrice(
    amount: Double,
    currencyCode: String,
): String

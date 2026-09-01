package com.nps.booknest

import androidx.compose.ui.window.ComposeUIViewController

// Swift imports this factory by its public PascalCase name.
@Suppress("FunctionNaming", "ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController { App() }

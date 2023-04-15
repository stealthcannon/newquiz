package com.infinitepower.newquiz.core_test.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.annotations.TestOnly
import java.util.Locale

@TestOnly
fun setDeviceLocale(
    context: Context,
    locale: Locale = Locale.ENGLISH
) {
    context.resources.apply {
        val config = Configuration(configuration)

        context.createConfigurationContext(configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, displayMetrics)
    }
}

@TestOnly
@Composable
@SuppressLint("ComposableNaming")
fun setDeviceLocale(
    locale: Locale = Locale.ENGLISH
) {
    setDeviceLocale(LocalContext.current, locale)
}

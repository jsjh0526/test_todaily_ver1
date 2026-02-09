package com.jsjh_todaily.test_todaily_ver1.data

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    // 가능한 값: "light", "dark", "system"
}

package com.example.githubprofile.helpers

import com.example.githubprofile.BuildConfig

object Config {
    const val SPLASH_SCREEN_DELAY: Long = 3000
    const val BASE_URL = "https://api.github.com"
    const val DEFAULT_USER_LOGIN = "depandiendapnb"
    val PERSONAL_ACCESS_TOKEN = "Bearer ${BuildConfig.ACCESS_TOKEN}"
}

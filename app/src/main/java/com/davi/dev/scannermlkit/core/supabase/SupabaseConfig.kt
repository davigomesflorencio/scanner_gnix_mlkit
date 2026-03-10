package com.davi.dev.scannermlkit.core.supabase

import com.davi.dev.scannermlkit.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

object SupabaseConfig {
    val supabaseUrl: String
        get() = BuildConfig.supabaseUrl

    val supabaseKey: String
        get() = BuildConfig.supabaseUrl

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.supabaseUrl,
            supabaseKey = BuildConfig.supabaseKey
        ) {
            install(Auth)
        }
    }

}
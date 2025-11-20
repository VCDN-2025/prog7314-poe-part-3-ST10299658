package com.skhaftin_poe

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val SELECTED_COUNTRY = "Locale.Helper.Selected.Country"

    fun onAttach(context: Context): Context {
        val lang = getPersistedLanguage(context)
        return setLocale(context, lang)
    }

    fun setLocale(context: Context, language: String, country: String = ""): Context {
        persist(context, language, country)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language, country)
        } else {
            updateResourcesLegacy(context, language, country)
        }
    }

    fun getPersistedLanguage(context: Context): String {
        val prefs = getPreferences(context)
        return prefs.getString(SELECTED_LANGUAGE, "en") ?: "en"
    }

    fun getCurrentLanguageName(context: Context): String {
        val languageCode = getPersistedLanguage(context)
        return when (languageCode) {
            "en" -> context.getString(R.string.english)
            "af" -> context.getString(R.string.afrikaans)
            else -> context.getString(R.string.english)
        }
    }

    private fun persist(context: Context, language: String, country: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.putString(SELECTED_COUNTRY, country)
        editor.apply()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String, country: String): Context {
        val locale = if (country.isNotEmpty()) {
            Locale(language, country)
        } else {
            Locale(language)
        }
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String, country: String): Context {
        val locale = if (country.isNotEmpty()) {
            Locale(language, country)
        } else {
            Locale(language)
        }
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}
package com.example.soulmatchapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.soulmatchapp.R
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // 🌙 Theme switch
            val themeSwitch = findPreference<SwitchPreferenceCompat>("dark_mode")
            themeSwitch?.setOnPreferenceChangeListener { _, newValue ->
                val darkMode = newValue as Boolean
                if (darkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                true
            }

            // 🌐 Language selector
            val languagePref = findPreference<ListPreference>("language")
            languagePref?.setOnPreferenceChangeListener { _, newValue ->
                setLocale(newValue.toString())
                true
            }
        }

        // Function to apply locale instantly
        private fun setLocale(languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
            requireActivity().recreate()
        }
    }
}

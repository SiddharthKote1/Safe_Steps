package com.Siddharth.SafeSteps

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            "user_preferences",
            Context.MODE_PRIVATE
        )

    private val KEY_NAME = "name"
    private val KEY_AGE = "age"
    private val KEY_PHONE1 = "phone1"
    private val KEY_PHONE2 = "phone2"
    private val KEY_COUNTRY_CODE1 = "country_code1"
    private val KEY_COUNTRY_CODE2 = "country_code2"

    private val KEY_OWN_PHONE = "own_phone"
    private val KEY_OWN_COUNTRY_CODE = "own_country_code"
    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_ACTIVE_SESSION_ID = "active_session_id"
    private val KEY_ACTIVE_TRACKING_ID = "active_tracking_id"

    private val KEY_SETUP_DONE = "setup_done"

    fun saveUserData(
        name: String,
        age: String,
        phone1: String,
        phone2: String,
        countryCode1: String,
        countryCode2: String
    ) {

        sharedPreferences.edit().apply {

            putString(KEY_NAME, name)
            putString(KEY_AGE, age)
            putString(KEY_PHONE1, phone1)
            putString(KEY_PHONE2, phone2)
            putString(KEY_COUNTRY_CODE1, countryCode1)
            putString(KEY_COUNTRY_CODE2, countryCode2)

            apply()
        }
    }

    fun getUserData(): User? {

        val name = sharedPreferences.getString(KEY_NAME, null)
        val age = sharedPreferences.getString(KEY_AGE, null)
        val phone1 = sharedPreferences.getString(KEY_PHONE1, null)
        val phone2 = sharedPreferences.getString(KEY_PHONE2, null)
        val countryCode1 =
            sharedPreferences.getString(KEY_COUNTRY_CODE1, null)
        val countryCode2 =
            sharedPreferences.getString(KEY_COUNTRY_CODE2, null)

        return if (
            name != null &&
            age != null &&
            phone1 != null &&
            phone2 != null &&
            countryCode1 != null &&
            countryCode2 != null
        ) {
            User(
                name = name,
                age = age,
                phone1 = phone1,
                phone2 = phone2,
                countryCode1 = countryCode1,
                countryCode2 = countryCode2
            )
        } else {
            null
        }
    }

    fun setAppSetupDone(value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_SETUP_DONE, value)
            .apply()
    }

    fun isAppSetupDone(): Boolean {
        return sharedPreferences.getBoolean(
            KEY_SETUP_DONE,
            false
        )
    }

    fun saveOwnPhone(phone: String, countryCode: String) {
        sharedPreferences.edit().apply {
            putString(KEY_OWN_PHONE, phone)
            putString(KEY_OWN_COUNTRY_CODE, countryCode)
            apply()
        }
    }

    fun getOwnPhone(): String? {
        return sharedPreferences.getString(KEY_OWN_PHONE, null)
    }

    fun getOwnCountryCode(): String? {
        return sharedPreferences.getString(KEY_OWN_COUNTRY_CODE, null)
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveActiveSession(sessionId: String, trackingId: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACTIVE_SESSION_ID, sessionId)
            putString(KEY_ACTIVE_TRACKING_ID, trackingId)
            apply()
        }
    }

    fun getActiveSessionId(): String? {
        return sharedPreferences.getString(KEY_ACTIVE_SESSION_ID, null)
    }

    fun getActiveTrackingId(): String? {
        return sharedPreferences.getString(KEY_ACTIVE_TRACKING_ID, null)
    }

    fun clearActiveSession() {
        sharedPreferences.edit().apply {
            remove(KEY_ACTIVE_SESSION_ID)
            remove(KEY_ACTIVE_TRACKING_ID)
            apply()
        }
    }

    fun clearUserData() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}

data class User(
    val name: String,
    val age: String,
    val phone1: String,
    val phone2: String,
    val countryCode1: String,
    val countryCode2: String
)
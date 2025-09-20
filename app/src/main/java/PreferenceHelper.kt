package com.example.chat

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    // Keys for storing user data
    private val KEY_NAME = "name"
    private val KEY_AGE = "age"
    private val KEY_DOB = "dob"
    private val KEY_PHONE1 = "phone1"
    private val KEY_PHONE2 = "phone2"
    private val KEY_COUNTRY_CODE1 = "country_code1"
    private val KEY_COUNTRY_CODE2 = "country_code2"

    // Save user data into SharedPreferences
    fun saveUserData(name: String, age: String, dob: String, phone1: String, phone2: String, countryCode1: String, countryCode2: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_AGE, age)
        editor.putString(KEY_DOB, dob)
        editor.putString(KEY_PHONE1, phone1)
        editor.putString(KEY_PHONE2, phone2)
        editor.putString(KEY_COUNTRY_CODE1, countryCode1)
        editor.putString(KEY_COUNTRY_CODE2, countryCode2)
        editor.apply()
    }

    private val KEY_SETUP_DONE = "setup_done"

    fun setAppSetupDone(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SETUP_DONE, value).apply()
    }

    fun isAppSetupDone(): Boolean {
        return sharedPreferences.getBoolean(KEY_SETUP_DONE, false)
    }

    // Retrieve saved user data from SharedPreferences
    fun getUserData(): User? {
        val name = sharedPreferences.getString(KEY_NAME, null)
        val age = sharedPreferences.getString(KEY_AGE, null)
        val dob = sharedPreferences.getString(KEY_DOB, null)
        val phone1 = sharedPreferences.getString(KEY_PHONE1, null)
        val phone2 = sharedPreferences.getString(KEY_PHONE2, null)
        val countryCode1 = sharedPreferences.getString(KEY_COUNTRY_CODE1, null)
        val countryCode2 = sharedPreferences.getString(KEY_COUNTRY_CODE2, null)

        return if (name != null && age != null && dob != null && phone1 != null && phone2 != null && countryCode1 != null && countryCode2 != null) {
            User(name, age, dob, phone1, phone2, countryCode1, countryCode2)
        } else {
            null
        }
    }

    // Clear the saved data (for example, if user logs out)
    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}

// Data class to hold user information
data class User(
    val name: String,
    val age: String,
    val dob: String,
    val phone1: String,
    val phone2: String,
    val countryCode1: String,
    val countryCode2: String
)
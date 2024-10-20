package com.hrithikvish.cbsm.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.SHARED_PREFERENCE_KEY,
        Context.MODE_PRIVATE
    )

    fun putBoolean(key: String?, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putObject(key: String?, `object`: Any?) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val objectString = gson.toJson(`object`)
        editor.putString(key, objectString)
        editor.apply()
    }

    fun getObject(key: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putInt(key: String?, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
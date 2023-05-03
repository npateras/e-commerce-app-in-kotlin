package com.unipi.mpsp21043.client.utils

import android.content.Context
import android.content.SharedPreferences
import com.unipi.mpsp21043.client.utils.PreferenceHelper.nightMode

object PreferenceHelper {

    fun defaultPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        when (val value = pair.second) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }

    var SharedPreferences.nightMode
        get() = getInt(Constants.PREF_NIGHT_MODE, 0)
        set(value) {
            editMe {
                it.putInt(Constants.PREF_NIGHT_MODE, value)
            }
        }

    var SharedPreferences.language
        get() = getString(Constants.PREF_LANGUAGE, "")
        set(value) {
            editMe {
                it.putString(Constants.PREF_LANGUAGE, value)
            }
        }

    var SharedPreferences.clearValues
        get() = run { }
        set(value) {
            editMe {
                it.nightMode(Constants.PREF_NIGHT_MODE)
                it.clear()
            }
        }
}

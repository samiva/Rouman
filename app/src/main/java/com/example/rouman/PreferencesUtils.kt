package com.example.rouman

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class PreferencesUtils {
    companion object{
        fun  getSettings(activityCompat: AppCompatActivity): ArrayList<String>  {
            var fields = ArrayList<String>()
            val prefs = activityCompat.getSharedPreferences(activityCompat.getString(R.string.Preference_file_key), Context.MODE_PRIVATE) ?: return fields
            val number = prefs.getString(activityCompat.getString(R.string.Preferences_phone_number), "0000000000")
            val R1 = prefs.getString(activityCompat.getString(R.string.Preferences_relay1), activityCompat.getString(R.string.NotAssigned))
            val R2 = prefs.getString(activityCompat.getString(R.string.Preferences_relay2), activityCompat.getString(R.string.NotAssigned))
            val R3 = prefs.getString(activityCompat.getString(R.string.Preferences_relay3), activityCompat.getString(R.string.NotAssigned))
            val R4 = prefs.getString(activityCompat.getString(R.string.Preferences_relay4), activityCompat.getString(R.string.NotAssigned))
            val R5 = prefs.getString(activityCompat.getString(R.string.Preferences_relay5), activityCompat.getString(R.string.NotAssigned))
            val R6 = prefs.getString(activityCompat.getString(R.string.Preferences_relay6), activityCompat.getString(R.string.NotAssigned))
            val R7 = prefs.getString(activityCompat.getString(R.string.Preferences_relay7), activityCompat.getString(R.string.NotAssigned))
            val R8 = prefs.getString(activityCompat.getString(R.string.Preferences_relay8), activityCompat.getString(R.string.NotAssigned))

            fields.add(number)
            fields.add(R1)
            fields.add(R2)
            fields.add(R3)
            fields.add(R4)
            fields.add(R5)
            fields.add(R6)
            fields.add(R7)
            fields.add(R8) 
            return fields
        }
    }
}
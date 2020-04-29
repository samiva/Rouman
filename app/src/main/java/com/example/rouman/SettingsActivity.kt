package com.example.rouman

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import org.jetbrains.anko.toast

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.settings, SettingsFragment())
//            .commit()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_confirm.setOnClickListener{
            val sharedpref = this.getSharedPreferences(getString(R.string.Preference_file_key), Context.MODE_PRIVATE)
            with(sharedpref.edit()) {

                fun setFieldValue(text: Editable, preference: Int) {
                    if(text.isNotEmpty()||text.isNotBlank()) {
                        toast("lol")
                        putString(getString(preference), text.toString())
                    }
                }

                val pattern = "^\\+?(\\d{10}|\\d{12})$"
                val regex = Regex(pattern)
                if(regex.matches(et_number.text.toString()))
                {
                    toast("match")
                    putString(getString(R.string.Preferences_phone_number), et_number.text.toString())
                }

                setFieldValue(et_R1.text, R.string.Preferences_relay1)
                setFieldValue(et_R2.text, R.string.Preferences_relay2)
                setFieldValue(et_R3.text, R.string.Preferences_relay3)
                setFieldValue(et_R4.text, R.string.Preferences_relay4)
                setFieldValue(et_R5.text, R.string.Preferences_relay5)
                setFieldValue(et_R6.text, R.string.Preferences_relay6)
                setFieldValue(et_R7.text, R.string.Preferences_relay7)
                setFieldValue(et_R8.text, R.string.Preferences_relay8)
                commit()
            }
//            val number = sharedpref.getString(getString(R.string.Preferences_phone_number), "wm")
//            toast(number)
        }
    }

//    class SettingsFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
}
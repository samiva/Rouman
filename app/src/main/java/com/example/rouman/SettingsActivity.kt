package com.example.rouman

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
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

        // If preferences already set, show them
        var settings = PreferencesUtils.getSettings(this)

        val R1_device = settings[1]
        val R2_device = settings[2]
        val R3_device = settings[3]
        val R4_device = settings[4]
        val R5_device = settings[5]
        val R6_device = settings[6]
        val R7_device = settings[7]
        val R8_device = settings[8]
        setRelayName(et_R1, R1_device)
        setRelayName(et_R2, R2_device)
        setRelayName(et_R3, R3_device)
        setRelayName(et_R4, R4_device)
        setRelayName(et_R5, R5_device)
        setRelayName(et_R6, R6_device)
        setRelayName(et_R7, R7_device)
        setRelayName(et_R8, R8_device)

        btn_confirm.setOnClickListener{
            val sharedpref = this.getSharedPreferences(getString(R.string.Preference_file_key), Context.MODE_PRIVATE)
            with(sharedpref.edit()) {

                fun setFieldValue(text: Editable, preference: Int) {
                    if(text.toString()!="") {

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

    private fun setRelayName(textEdit: EditText?, text:String) {
        textEdit?.hint = text
    }

//    class SettingsFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
}
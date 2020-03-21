package com.example.rouman

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class MessageSender {
    companion object {
        fun sendMessageToController(context: Context, msg: String, number:String) {
            Log.i("Rouman_sms", "Send SMS")
            // NOTE: Needs human intervention
            /*val intent = Intent( Intent.ACTION_SENDTO,Uri.parse("smsto:"))

            intent.putExtra("address", number)
            intent.putExtra("sms_body", msg)
            startActivity(context,intent,null)*/
            val sms = SmsManager.getDefault() as SmsManager
            sms.sendTextMessage(number, null, msg, null, null)

        }
    }
}
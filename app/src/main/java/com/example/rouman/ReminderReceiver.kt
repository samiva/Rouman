package com.example.rouman

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rouman.MessageSender.Companion.sendMessageToController
import org.jetbrains.anko.toast

class ReminderReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //val text  = intent.getStringExtra("message")
        context!!.toast("Nyt SMS pitäs lähteä")

        //val msdr = MessageSender()
        var msg = "Releasetukset: "+ intent?.getStringExtra("relay") + "=" + intent?.getStringExtra("setting") + "/"
        sendMessageToController(context, msg, "0505617080")
    }
}
package com.example.rouman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    override fun onResume(){
        super.onResume()

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "controlEvents").build()
            val cEventList = db.controlEventDao().getControlEvents()
            db.close()

            uiThread {
                if (cEventList.isNotEmpty()) {
                    val adapter = ControlEventAdapter(applicationContext, cEventList )
                    list_in_view.adapter = adapter
                } else {
                    toast("No reminders")
                }
            }

        }
    }
}

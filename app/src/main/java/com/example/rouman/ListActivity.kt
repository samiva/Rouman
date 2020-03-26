package com.example.rouman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import com.example.rouman.MainActivity.Companion.cEventList
import kotlinx.android.synthetic.main.activity_main.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val button_clear = findViewById<View>(R.id.button_clear) as Button
        button_clear.setOnClickListener {

            doAsync {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "control_events"
                ).build()
                db.controlEventDao().clearDb()
                db.close()

                onResume()
            }
        }
    }
    override fun onResume(){
        super.onResume()

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "control_events").build()
            cEventList = db.controlEventDao().getControlEvents()
            db.close()

            uiThread {
                if (cEventList.isNotEmpty()) {
                    val adapter = ControlEventAdapter(applicationContext, cEventList )
                    list_in_view.adapter = adapter
                } else {
                    toast("No control events")
                }
            }

        }
    }
}

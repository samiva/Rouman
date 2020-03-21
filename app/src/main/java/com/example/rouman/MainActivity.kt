package com.example.rouman

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.internals.AnkoInternals.getContext
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.milliseconds
import android.widget.TextView as TextView1

import kotlinx.android.synthetic.main.activity_main.*

import androidx.room.Database

class MainActivity : AppCompatActivity() {
    private val neededPermissions = arrayOf(android.Manifest.permission.SEND_SMS)

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in neededPermissions) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if(permissionsToRequest.count()>0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_list.setOnClickListener {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }

        val button_plat = findViewById<View>(R.id.button12) as Button
        button_plat.setOnClickListener {
            propoStatus += 1
            if (propoStatus == 4)
                propoStatus = 0

            propoY = button_plat.getTop().toFloat()
            if (propoStatus == 1) {
                proposedRelay = "PLAT"
                proposedStatus = "1"
            }
            if (propoStatus == 1) {
                proposedRelay = "PLAT"
                proposedStatus = "2"

            }
            if (propoStatus == 1) {
                proposedRelay = "PLAT"
                proposedStatus = "3"
            }
            val c = findViewById<View>(R.id.canvasView) as Canvass
            c.invalidate()
        }

//        val button_confirm = findViewById<View>(R.id.button_confirm) as Button
        button_confirm.setOnClickListener {

            // Jos on tekstiä JA aika kalenterista on suurempi kuin systeemiaika
             if (changeProposed) {

                val controlEvent = ControlEvent(
                    uid = null,
                    time = globalTimeSet,
                    relay = proposedRelay,
                    setting = proposedStatus
                )

                doAsync {
                    val db =
                        Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "control_events"
                            )
                            .build()
                    db.controlEventDao().insert(controlEvent)
                    db.close()

//                        setAlarm(reminder.time!!, reminder.message)

//                    finish()
                    uiThread {
                        toast("Change saved and alarm created")
                    }

                }
//                changeProposed = false
            }
        }
    }

    private fun setAlarm(time: Long, controlEvent: ControlEvent){
/*        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("message",message)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC,time,pendingIntent)

        runOnUiThread{toast("Reminder is created")}
*/
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
*/


    override fun onResume() {
        super.onResume()

        val sdf_show = SimpleDateFormat("E dd.MM")
        val sdf_dayNumber = SimpleDateFormat("u")
        val sdf_now = SimpleDateFormat("dd.MM")

        val tz = java.util.TimeZone.getDefault()

        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        // Aika nyt
        var syTime = System.currentTimeMillis()

        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        // Aika viikon alussa
        var sdf_year = SimpleDateFormat("yyyy")
        var sdf_month = SimpleDateFormat("MM")
        var sdf_day = SimpleDateFormat("dd")
        var sdf_h = SimpleDateFormat("HH")
        var sdf_m = SimpleDateFormat("mm")


        sdf_year.timeZone = tz
        sdf_month.timeZone = tz
        sdf_day.timeZone = tz
        sdf_h.timeZone = tz
        sdf_m.timeZone = tz

        val year = sdf_year.format(syTime).toInt()
        val month = sdf_month.format(syTime).toInt()
        val day = sdf_day.format(syTime).toInt()
        var dayNumber = sdf_dayNumber.format(syTime).toInt()
        var h= sdf_h.format(syTime).toInt()
        var m = sdf_m.format(syTime).toInt()

        ////////////////////////////////////////////////////////////
        // KORJAA !!  KUN KUUKAUSI VAIHTUU kesken viikon
        ////////////////////////////////////////////////////////////

        var calendar = GregorianCalendar(
            year,
            month,
            day - dayNumber + 1,
            0, //timePicker.currentHour,
            0
        )
        timeOnWeekStart = calendar.timeInMillis

        ///////////////////////////////////////////////
        ///////////////////////////////////////////////
        // Aika viikon lopussa
        calendar = GregorianCalendar(
            year,
            month,
            day - dayNumber + 1+7,
            0, //timePicker.currentHour,
            0
        )

        timeOnWeekEnd = calendar.timeInMillis


        ////////////////////////////////////////////////
        // Muunnoskertoimet

        deltaTime = timeOnWeekEnd - timeOnWeekStart
        //val canvasV = findViewById<View>(R.id.canvasView) as Canvass

        // Current time to screen
        val readableTime = sdf_show.format(syTime)
//        val readableTime = sdf_show.format(timeOnWeekStart)

        val tv = findViewById<android.widget.TextView>(R.id.text_timeNow)
        if (tv != null)
           {tv.text = readableTime}

        calendar = GregorianCalendar(
            year,
            month,
            day,
            h,
            m
        )
        curTime = calendar.timeInMillis


        //////////////////////////////
        // Read database
        refreshList()
    }

    private fun refreshList(){
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "controlEvents").build()
            val cEventList = db.controlEventDao().getControlEvents()
            db.close()

// tätä voisi muokata jos haluaisi control eventit tekstimuodossa tai jossain muussa tyylissä näkyviin vaikka toiseen UI:hin
/*
            uiThread {
                if (reminders.isNotEmpty()) {
                    val adapter = ReminderAdapter(applicationContext, reminders )
                    list1.adapter = adapter
                } else {
                    toast("No reminders")
                }
            }
*/
        }

    }

    ///////////////////////////////////////////////////////////////
    // Piirretään tietokanta ruutuun, vaihtaen painttia CanvasViewissä

    ///////////////////////////////////////////////////////////////
    // Muuteteaan ehdotusta nappuloilla aina setTimesta eteenpäin
    // Ehdotuksen arvo togglaa 0,1,2
    // Piirretään ehdotukseen mukaan canvasin on drawissa

    ///////////////////////////////////////////////////////////////
    // OK;lla kirjoitetaan eventti tietokantaan
    // Vain jos ehdotus on != nykyinen



    companion object {

        var globalDpSet = 0f

        var globalTimeSet: Long = 10
        var timeOnWeekStart: Long = 1
        var timeOnWeekEnd: Long = 1
        var curTime: Long =1
        var deltaTime: Long = 578

        var timeToDp = 1f
        var dpToTime = 1f

        var changeProposed=true

        var proposedRelay = ""
        var proposedStatus = ""
        var propoStatus = 0
        var propoY = 0f
    }
}


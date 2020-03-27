package com.example.rouman

//////////////////////////////////////////////////////////////////////////////////////////
// Copyright Vesa Similä & Sami Varanka
// spring 2020

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.rouman.MessageSender.Companion.sendMessageToController
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.listview_item.*
import kotlinx.android.synthetic.main.listview_item.view.*
import org.jetbrains.anko.uiThread
import java.time.LocalDateTime
import java.time.LocalTime

///////////////////////////////////////////////////////////////
// Piirretään tietokanta ruutuun, vaihtaen painttia CanvasViewissä

///////////////////////////////////////////////////////////////
// Muuteteaan ehdotusta nappuloilla aina setTimesta eteenpäin
// Ehdotuksen arvo togglaa 0,1,2
// Piirretään ehdotukseen mukaan canvasin on drawissa

///////////////////////////////////////////////////////////////
// OK;lla kirjoitetaan eventti tietokantaan
// Vain jos ehdotus on != nykyinen && poistetaan seuraava jos turha uuden jälkeen


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
        checkPermissions()

        button_list.setOnClickListener {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }

        button_send_now.setOnClickListener{
            toast("Nyt SMS pitäs lähteä testiviesti")

            var msg = "Releasetukset: PLAT" + "= 0" + "/"
            sendMessageToController(applicationContext, msg, "0505617080")
        }

        button12.setOnClickListener {
            proposedRelay = "PLAT"
            propoY = button12.getTop().toFloat()
            stepPropoStatus()
        }
        button13.setOnClickListener {
            proposedRelay = "VARV"
            propoY = button13.getTop().toFloat()
            stepPropoStatus()
        }
        button14.setOnClickListener {
            proposedRelay = "VARK"
            propoY = button14.getTop().toFloat()
            stepPropoStatus()
        }
        button15.setOnClickListener {
            proposedRelay = "VARO"
            propoY = button15.getTop().toFloat()
            stepPropoStatus()
        }
        button16.setOnClickListener {
            proposedRelay = "PUMP"
            propoY = button16.getTop().toFloat()
            stepPropoStatus()
        }
        button17.setOnClickListener {
            proposedRelay = "KVES"
            propoY = button17.getTop().toFloat()
            stepPropoStatus()
        }
        buttonR7.setOnClickListener {
            proposedRelay = "R7"
            propoY = buttonR7.getTop().toFloat()
            stepPropoStatus()
        }
        buttonR8.setOnClickListener {
            proposedRelay = "R8"
            propoY = buttonR8.getTop().toFloat()
            stepPropoStatus()
        }



        /////////////////////////////////////////////////////////////////////////
        // Luodaan OK buttonin toiminta
        val button_confirm = findViewById<View>(R.id.button_confirm) as Button
        button_confirm.setOnClickListener {

            //////////////////////////////////////////////////////////////
            // Mikä on seuraava, pitääkö poistaa
            // cEvent sisältää kaikki eventit aikajärjestyksessä alkaen uusimmasta
            var nextEvent: ControlEvent? = null
            var currentEvent: ControlEvent? = null
            for(event in cEventList.filter{ r -> r.relay == proposedRelay}) {
                if(timeSet<=event.time!!) {
                    //UID
                    nextEvent = event
                }
                if (timeSet>event.time!!){
                    if(currentEvent == null)
                        currentEvent = event
                }
            }

            ///////////////////////////////////////////
            // Remove if re-setting
            if(nextEvent!=null) {
                if (nextEvent!!.setting == proposedStatus || nextEvent.time == timeSet) {
                    doAsync {
                        val db =
                            Room.databaseBuilder(
                                    applicationContext,
                                    AppDatabase::class.java,
                                    "control_events"
                                )
                                .build()
                        db.controlEventDao().deleteRowByData(
                            time = nextEvent!!.time,
                            relay = nextEvent!!.relay,
                            setting = nextEvent!!.setting
                        )
                        db.close()
                    }
                }
            }

            //////////////////////////////////////////////
            // If current setting is same do not save new
//            if (currentEvent != null) {
                if (currentEvent?.setting == proposedStatus) {
                    toast("Same setting exists already")
                } else {
                    // Jos on tekstiä JA aika kalenterista on suurempi kuin systeemiaika
                    if (changeProposed && proposedRelay != "" && proposedStatus != "") {

                        val newEvent = ControlEvent(
                            uid = null,
                            time = timeSet,
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
                            db.controlEventDao().insert(newEvent)
                            db.close()

                            toast("Change saved and alarm created")
                        }

                        changeProposed = false
                        proposedRelay = ""
                        proposedStatus = ""
                        propoStatus = 0

                        refreshList()

                        canvasView.invalidate()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
  //          }
        }
    }

    private fun stepPropoStatus(){
        propoStatus += 1
        changeProposed = false
        if (propoStatus == 4) {
            propoStatus = 0
            proposedStatus = ""
            changeProposed=false
        }
        if (propoStatus == 1) {
            proposedStatus = "1"
            changeProposed=true
        }
        if (propoStatus == 2) {
            proposedStatus = "2"
            changeProposed=true

        }
        if (propoStatus == 3) {
            proposedStatus = "0"
            changeProposed=true
        }
//        val c = findViewById<View>(R.id.canvasView) as Canvass
        canvasView.invalidate()
    }



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

    private fun setAlarm(controlEvent: ControlEvent){
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("relay",controlEvent.relay)
        intent.putExtra("setting",controlEvent.setting)
        val currentTime = System.currentTimeMillis()
        val futureTime = currentTime+10000 // ten seconds in future
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, futureTime,pendingIntent)
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

    private fun refreshList(){
        doAsync {

            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "control_events").build()
            cEventList = db.controlEventDao().getControlEvents()
            db.close()

            ///////////////////////////////////////////////////////////////////////////////////
            // Pitäisi varmaan  tässä poistaa olemassaolevat ja luoda uudet remiderit muistiin?
            // setAlarm(reminder.time!!, reminder.message)

            uiThread {
                for (event in cEventList) {
                   setAlarm(event)
                }

                canvasView.invalidate()
                runOnUiThread{toast("Reminders are created")}
            }
        }
    }



    companion object {

        var cEventList: List<ControlEvent> = emptyList()

        var timeSetDp = 0f
        var timeSet: Long = 10

        var timeOnWeekStart: Long = 1
        var timeOnWeekEnd: Long = 1

        var curTime: Long =1
        var curTimeDp: Float = 1f
        var deltaTime: Long = 578

        var timeToDp = 1f
        var dpToTime = 1f

        var changeProposed=false

        var proposedRelay = ""
        var proposedStatus = ""
        var propoStatus = 0
        var propoY = 0f
    }
}


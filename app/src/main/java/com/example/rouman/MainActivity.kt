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
import android.view.OrientationEventListener
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.rouman.MessageSender.Companion.sendMessageToController
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.uiThread

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
    private var orientationEventListener: OrientationEventListener? = null

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

        setRelayNames()

        button_list.setOnClickListener {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }

        button_send_now.setOnClickListener{
            toast("Nyt SMS pitäs lähteä testiviesti")

            var msg = "Releasetukset: PLAT" + "= 0" + "/"
            if(phoneNumber!="0000000000")
                sendMessageToController(applicationContext, msg, phoneNumber)
        }

        btn_R1.setOnClickListener {
            if(btn_R1.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "PLAT"
            propoY = btn_R1.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R2.setOnClickListener {
            if(btn_R2.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "VARV"
            propoY = btn_R2.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R3.setOnClickListener {
            if(btn_R3.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "VARK"
            propoY = btn_R3.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R4.setOnClickListener {
            if(btn_R4.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "VARO"
            propoY = btn_R4.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R5.setOnClickListener {
            if(btn_R5.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "PUMP"
            propoY = btn_R5.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R6.setOnClickListener {
            if(btn_R6.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "KVES"
            propoY = btn_R6.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R7.setOnClickListener {
            if(btn_R7.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "R7"
            propoY = btn_R7.getTop().toFloat()
            stepPropoStatus()
        }
        btn_R8.setOnClickListener {
            if(btn_R8.text.toString()==getString(R.string.NotAssigned))
            {
                toast("Erska")
                return@setOnClickListener
            }
            proposedRelay = "R8"
            propoY = btn_R8.getTop().toFloat()
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
            var sameTimeEvent: ControlEvent? = null
            for(event in cEventList.filter{ r -> r.relay == proposedRelay}) {
                if(timeSet<event.time!!) {
                    nextEvent = event
                }
                if(timeSet==event.time!!) {
                    sameTimeEvent = event
                }
                if (timeSet>event.time!!){
                    if(currentEvent == null)
                        currentEvent = event
                }
            }

            ///////////////////////////////////////////
            // Remove if re-setting
            if(nextEvent!=null) {
                if (nextEvent.setting == proposedStatus) {
                    doAsync {
                        val db =
                            Room.databaseBuilder(
                                    applicationContext,
                                    AppDatabase::class.java,
                                    "control_events"
                                )
                                .build()
                        db.controlEventDao().deleteRowByData(
                            time = nextEvent.time,
                            relay = nextEvent.relay,
                            setting = nextEvent.setting
                        )
                        db.close()
                    }
                }
            }

            ///////////////////////////////////////////
            // Remove if same Time span
            if(sameTimeEvent!=null) {
                doAsync {
                    val db =
                        Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "control_events"
                            )
                            .build()
                    db.controlEventDao().deleteRowByData(
                        time = sameTimeEvent.time,
                        relay = sameTimeEvent.relay,
                        setting = sameTimeEvent.setting
                    )
                    db.close()
                }
            }

            //////////////////////////////////////////////
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

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }

        orientationEventListener = object : OrientationEventListener(applicationContext) {
            override fun onOrientationChanged(orientation: Int) {
                // orientation is in degrees
                toast("Changed")
                timeSetDp = 0f
            }
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
        setRelayNames()
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

/*
        sdf_year.timeZone = tz
        sdf_month.timeZone = tz
        sdf_day.timeZone = tz
        sdf_h.timeZone = tz
        sdf_m.timeZone = tz
*/
        val year = sdf_year.format(syTime).toInt()
        val month = sdf_month.format(syTime).toInt()
        val day = sdf_day.format(syTime).toInt()
        var dayNumber = sdf_dayNumber.format(syTime).toInt()
        var h= sdf_h.format(syTime).toInt()
        var m = sdf_m.format(syTime).toInt()

        ////////////////////////////////////////////////////////////
        // KORJAA !!  KUN KUUKAUSI VAIHTUU kesken viikon
        ////////////////////////////////////////////////////////////
// var
        calendar = GregorianCalendar(
            year,
            month-1,
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
            month-1,
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
            month-1,
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
        var delta = calendar.timeInMillis - System.currentTimeMillis()
        val futureTime = controlEvent.time!! - delta
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, futureTime!!,pendingIntent)
    }

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
                    if( curTime < event.time!!)
                        setAlarm(event)
                }
            }
        }
    }

    private fun setRelayNames() {
        val settings = PreferencesUtils.getSettings(this)
        val number = settings[0]
        val R1_device = settings[1]
        val R2_device = settings[2]
        val R3_device = settings[3]
        val R4_device = settings[4]
        val R5_device = settings[5]
        val R6_device = settings[6]
        val R7_device = settings[7]
        val R8_device = settings[8]
        setRelayName(btn_R1, R1_device)
        setRelayName(btn_R2, R2_device)
        setRelayName(btn_R3, R3_device)
        setRelayName(btn_R4, R4_device)
        setRelayName(btn_R5, R5_device)
        setRelayName(btn_R6, R6_device)
        setRelayName(btn_R7, R7_device)
        setRelayName(btn_R8, R8_device)

        phoneNumber = number
    }

    private fun setRelayName(btn:Button?, text:String) {
         btn?.text = text
    }

    companion object {
        var phoneNumber: String = ""
        var calendar = GregorianCalendar()
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


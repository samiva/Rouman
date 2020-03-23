package com.example.rouman

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_list.*
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
            changeProposed = false
            if (propoStatus == 4) {
                propoStatus = 0
                changeProposed=false
            }
            propoY = button_plat.getTop().toFloat()
            if (propoStatus == 1) {
                proposedRelay = "PLAT"
                proposedStatus = "1"
                changeProposed=true
            }
            if (propoStatus == 2) {
                proposedRelay = "PLAT"
                proposedStatus = "2"
                changeProposed=true

            }
            if (propoStatus == 3) {
                proposedRelay = "PLAT"
                proposedStatus = "0"
                changeProposed=true
            }
            val c = findViewById<View>(R.id.canvasView) as Canvass
            c.invalidate()
        }


        /////////////////////////////////////////////////////////////////////////
        // Luodaan OK buttonin toiminta
        val button_confirm = findViewById<View>(R.id.button_confirm) as Button
        button_confirm.setOnClickListener {

            //////////////////////////////////////////////////////////////
            // Mikä on seuraava, pitääkö poistaa
            //var nextSetting = width.toFloat()
            // cEvent sisältää kaikki eventit aikajärjestyksessä alkaen uusimmasta
            var nextEventStatus = "0"
            var nextEvent: ControlEvent? = null
            var currentEvent: ControlEvent? = null
            for(event in cEventList) {
                if(event.relay == "PLAT") {
                    if(timeSet<event.time!!) {
                        //UID
                        nextEventStatus = event.setting
                        nextEvent = event
                    }
                    else{
                        if(currentEvent == null)
                            currentEvent = event
                    }
                }
            }


            ///////////////////////////////////////////
            // Remove if re-setting - This cehcekd and works
            if (nextEventStatus == proposedStatus){
                doAsync {
                    val db =
                        Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "control_events"
                            )
                            .build()
                    db.controlEventDao().deleteRowByData(
                        time = nextEvent?.time,
                        relay = nextEvent?.relay,
                        setting =nextEvent?.setting)
                    db.close()
                }
            }

           if(currentEvent?.setting == proposedStatus ) {
               toast("Same setting exists already")
           }
            else{
                // Jos on tekstiä JA aika kalenterista on suurempi kuin systeemiaika
                if (changeProposed && proposedRelay!="" && proposedStatus!="") {

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

                     val c = findViewById<View>(R.id.canvasView) as Canvass
                     c.invalidate()
                }
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
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "control_events").build()
            cEventList = db.controlEventDao().getControlEvents()
            db.close()

            ///////////////////////////////////////////////////////////////////////////////////
            // Pitäisi varmaan  tässä poistaa olemassaolevat ja luoda uudet remiderit muistiin?
            // setAlarm(reminder.time!!, reminder.message)

            val c = findViewById<View>(R.id.canvasView) as Canvass
            c.invalidate()
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


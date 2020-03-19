package com.example.rouman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.internals.AnkoInternals.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.milliseconds
import android.widget.TextView as TextView1


class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

/*
        val bu =
            findViewById<View>(R.id.button_confirm) as Button
        bu.setOnClickListener {

            // Do something in response to button click
//            customCanvas.clearCanvas() // to clear canvas
        }
*/

/*
        val textSetView =
            findViewById<TextView1>(R.id.text_timeSet) as TextView1
        textSetView.setText(globalTimeSet.toString())  //settingLineX.toString()

        var context = getContext(this)
        class tView : TextView1(context){
            override fun onDraw(canvas: Canvas){
                super.onDraw(canvas)
                textSetView.setText(globalTimeSet.toString())  //settingLineX.toString()
           }
        }
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
        // Aika nyt
        var syTime = System.currentTimeMillis()

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

        //////////////
        // KORJAA REUNAPÖIVÄKUNM KUUKAUSI VAIHTUU
        var calendar = GregorianCalendar(
            year,
            month,
            day - dayNumber + 1,
            0, //timePicker.currentHour,
            0
        )

        timeOnWeekStart = calendar.timeInMillis


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

    }

    companion object {
        var globalDpSet = 0f

        var globalTimeSet: Long = 10
        var timeOnWeekStart: Long = 1
        var timeOnWeekEnd: Long = 1
        var curTime: Long =1
        var deltaTime: Long = 578

        var timeToDp = 1f
        var dpToTime = 1f
    }
}

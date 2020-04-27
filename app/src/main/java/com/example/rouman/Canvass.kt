// Globaalit muuttujat
// getviewid alaluokassa
// tietokannan luonti
// Tietueen luonti
// onDraw


package com.example.rouman;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface.DEFAULT_BOLD
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.Gravity.BOTTOM
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridLayout.BOTTOM
import android.widget.TextView
import com.example.rouman.MainActivity.Companion.cEventList
import com.example.rouman.MainActivity.Companion.curTime
import com.example.rouman.MainActivity.Companion.curTimeDp
import com.example.rouman.MainActivity.Companion.dpToTime
import com.example.rouman.MainActivity.Companion.propoStatus
import com.example.rouman.MainActivity.Companion.proposedRelay
import com.example.rouman.MainActivity.Companion.timeOnWeekEnd
import com.example.rouman.MainActivity.Companion.timeOnWeekStart
import com.example.rouman.MainActivity.Companion.timeSet
import com.example.rouman.MainActivity.Companion.timeSetDp
import com.example.rouman.MainActivity.Companion.timeToDp
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import kotlin.math.abs
import kotlin.math.roundToLong


class Canvass(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mX = 0f
    private var mY = 0f


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)

        /////////////////////////////////
        // Pitäisköhän setting linen asetusarvot laskea tässä uusiksi jotta landscape ja portait muunnos menis hyvin?
    }

    // override onDraw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = getWidth()
        val height = getHeight().toFloat()
        var paint = Paint()

        //////////////////////////////////////////////////////////////////
        // Draw daily lines
        paint.setARGB(255, 255, 0, 255)
        paint.setStrokeWidth(4f)
        drawLineZ(width / 7 * 1.toFloat(), 0f, width / 7 * 1.toFloat(), height, paint, canvas)
        drawLineZ(width / 7 * 2.toFloat(), 0f, width / 7 * 2.toFloat(), height, paint, canvas)
        drawLineZ(width / 7 * 3.toFloat(), 0f, width / 7 * 3.toFloat(), height, paint, canvas)
        drawLineZ(width / 7 * 4.toFloat(), 0f, width / 7 * 4.toFloat(), height, paint, canvas)
        drawLineZ(width / 7 * 5.toFloat(), 0f, width / 7 * 5.toFloat(), height, paint, canvas)
        drawLineZ(width / 7 * 6.toFloat(), 0f, width / 7 * 6.toFloat(), height, paint, canvas)

 //       var t = getRootView().findViewById<Button>(R.id.text_timeSet).top.toFloat()
        var t = 20f
        drawTextZ("MAANANTAI", 0.5f, t, canvas)
        drawTextZ("TIISTAI", width / 7 * 1.5.toFloat(), t, canvas)
        drawTextZ("KESKIVIIKKO", width / 7 * 2.5.toFloat(), t, canvas)
        drawTextZ("TORSTAII", width / 7 * 3.5.toFloat(), t, canvas)
        drawTextZ("PERJANTAI", width / 7 * 4.5.toFloat(), t, canvas)
        drawTextZ("LAUANTAI", width / 7 * 5.5.toFloat(), t, canvas)
        drawTextZ("SUNNUNTAI", width / 7 * 6.5.toFloat(), t, canvas)

        ///////////////////////////////////////////////
        // Draw current timeline

        val pros = ((curTime - timeOnWeekStart) / (timeOnWeekEnd - timeOnWeekStart).toFloat())

        timeToDp = width.toFloat() / (timeOnWeekEnd - timeOnWeekStart).toFloat()
        dpToTime = 1 / timeToDp

        paint.setARGB(255, 0, 0, 0)
        paint.setStrokeWidth(4f)

        curTimeDp = (curTime - timeOnWeekStart) * timeToDp
        drawLineZ(curTimeDp, 0f, curTimeDp, height, paint, canvas)

        ////////////////////////////////////////////////////
        // Draw relay program line
        drawProgram("PLAT", canvas)
        drawProgram("VARV", canvas)
        drawProgram("VARK", canvas)
        drawProgram("VARO", canvas)
        drawProgram("PUMP", canvas)
        drawProgram("KVES", canvas)
        drawProgram("R7", canvas)
        drawProgram("R8", canvas)

        //////////////////////////////////////////////////////////////////
        // Draw setting line
        paint.setARGB(255, 255, 0, 0)
        paint.setStrokeWidth(4f)
        drawLineZ(timeSetDp.toFloat(), 0f, timeSetDp.toFloat(), height, paint, canvas)
//        drawLineZ(timeSet/timeToDp, 0f, timeSet/timeToDp.toFloat(), height, paint,canvas)


        /////////////////////////////////////////////////////////////
        // Draw proposal
        val a = 40
//        var h = getRootView().findViewById<Button>(R.id.text_timeSet).height.toFloat()*2
        var h=0f
        paint.textSize = 15f
        if (proposedRelay == "PLAT")
            t = getRootView().findViewById<Button>(R.id.button12).top.toFloat()
        if (proposedRelay == "VARV")
            t = getRootView().findViewById<Button>(R.id.button13).top.toFloat()
        if (proposedRelay == "VARK")
            t = getRootView().findViewById<Button>(R.id.button14).top.toFloat()
        if (proposedRelay == "VARO")
            t = getRootView().findViewById<Button>(R.id.button15).top.toFloat()
        if (proposedRelay == "PUMP")
            t = getRootView().findViewById<Button>(R.id.button16).top.toFloat()
        if (proposedRelay == "KVES")
            t = getRootView().findViewById<Button>(R.id.button17).top.toFloat()
        if (proposedRelay == "R7")
            t = getRootView().findViewById<Button>(R.id.buttonR7).top.toFloat()
        if (proposedRelay == "R8")
            t = getRootView().findViewById<Button>(R.id.buttonR8).top.toFloat()

        drawProposal(t - h + a, canvas)
        canvas.drawText("Proposal", 0f, t, paint)

    }

    ////////////////////////////////////////////////////
    // Draw relay program line
    private fun drawProgram(relToDraw: String, canvas: Canvas) {
        var paint = Paint()
        var plat_y: Float = 0f
        val a = 20
//        var h = getRootView().findViewById<Button>(R.id.text_timeSet).height.toFloat()*2
        var h=0f
        if (relToDraw == "PLAT")
            plat_y = getRootView().findViewById<Button>(R.id.button12).top.toFloat() - h + a
        if (relToDraw == "VARV")
            plat_y = getRootView().findViewById<Button>(R.id.button13).top.toFloat() - h + a
        if (relToDraw == "VARK")
            plat_y = getRootView().findViewById<Button>(R.id.button14).top.toFloat() - h + a
        if (relToDraw == "VARO")
            plat_y = getRootView().findViewById<Button>(R.id.button15).top.toFloat() - h + a
        if (relToDraw == "PUMP")
            plat_y = getRootView().findViewById<Button>(R.id.button16).top.toFloat() - h + a
        if (relToDraw == "KVES")
            plat_y = getRootView().findViewById<Button>(R.id.button17).top.toFloat() - h + a
        if (relToDraw == "R7")
            plat_y = getRootView().findViewById<Button>(R.id.buttonR7).top.toFloat() - h + a
        if (relToDraw == "R8")
            plat_y = getRootView().findViewById<Button>(R.id.buttonR8).top.toFloat() - h + a


        var endXTime = curTime + timeOnWeekEnd - timeOnWeekStart
        var endX = endXTime / dpToTime
        endX = curTimeDp + width

        var startX = 0f
        for (event in cEventList.filter { r -> r.relay == relToDraw }) {
            if (event.setting == "0")
                paint.setARGB(255, 0, 0, 0)
            if (event.setting == "1")
                paint.setARGB(255, 255, 0, 0)
            if (event.setting == "2")
                paint.setARGB(255, 0, 0, 255)
            paint.setStrokeWidth(20f)

            var eventTime = event.time!!.toLong()
            var startXTime = eventTime
            startX = (eventTime - timeOnWeekStart) * timeToDp

            if (startXTime < timeOnWeekEnd && endX > curTimeDp && endX <= width) { // Jos molemmat on välillä curtime - width
                if (startX < curTimeDp) {
                    startX = curTimeDp // viimeinen voi alkaa ennen curtime
                }
                if (endX > curTimeDp) { // Piirretään vain jos loppupää on canvasin alueella
                    drawLineZ(startX, plat_y, endX, plat_y, paint, canvas)
                    endX = startX // Seuraavanloppu on tämän alku
                }
            }
            if (startXTime < timeOnWeekEnd && endX > width) { // Eli jos on taitekohta pitää piirtää kahdessa välissä
                // Näitä pitäs olla vain yks
                drawLineZ(0f, plat_y, endX - width, plat_y, paint, canvas)
                endX = width.toFloat() // jatkopala loppuu canvasin oikeaan reunaan
                drawLineZ(startX, plat_y, width.toFloat(), plat_y, paint, canvas)
                endX = startX // Seuraava loppuu tähän
            }
            if (startXTime > timeOnWeekEnd && endX > width) { // Jos on alku ja loppu width:n jälkeen, eli välillä 0-curtime
                drawLineZ(startX - width, plat_y, endX - width, plat_y, paint, canvas)
                endX = startX // Seuraavanloppu on tämän alku
            }
        }
    }

    private fun drawLineZ(x1: Float, y1: Float, x2: Float, y2: Float, p: Paint, canvas: Canvas) {
        var alfa = 1 + 0.01 * zoom
        var a: Float = (alfa * x1).toFloat()
        var b: Float = (alfa * x2).toFloat()

        if (zoom != 0) {
            siirto = (alfa * timeSetDp - width / 2).toFloat()
            a = a - siirto
            b = b - siirto
        }
        canvas.drawLine(a, y1, b, y2, p)
    }

    private fun drawTextZ(str: String, x1: Float, y: Float , canvas: Canvas){
        var pa = Paint()
        pa.textSize = 12f + zoom/12
        if (pa.textSize > 100) pa.textSize = 100f
        pa.typeface = DEFAULT_BOLD
//        pa.textScaleX = zoom/1000+1f
        pa.textAlign = Paint.Align.CENTER
        val textBound = Rect()
        pa.getTextBounds(str,0,str.length, textBound)

        var alfa = 1 + 0.01 * zoom
        var a: Float = (alfa * x1).toFloat()

        if (zoom != 0) {
            siirto = (alfa * timeSetDp - width / 2).toFloat()
            a = a - siirto
        }

        canvas.drawText(str,a, y + textBound.height(), pa)
    }

    private fun drawProposal(propoY: Float, canvas: Canvas){

        val width = canvas.getWidth()
        var paint = Paint()
        var s=7
        /////////////////////////////////////////////////////////7
        // Endpoint for proposal
        var propoEnd = curTimeDp + width
        for(event in cEventList.filter{r->r.relay == proposedRelay}) {
            if(timeSet<event.time!! ) // Etsitään asetettuun aikaan nähden seuraava eventti
                propoEnd = (event.time!! - timeOnWeekStart) * timeToDp
        }

        if(propoStatus != 0) {
            if (propoStatus == 1)
                paint.setARGB(255, 255, 0, 0)
            if (propoStatus == 2)
                paint.setARGB(255, 0, 0, 255)
            if (propoStatus == 3)
                paint.setARGB(255, 0, 0, 0)
            paint.setStrokeWidth(14f)

            if(propoEnd <= timeOnWeekEnd* timeToDp && timeSetDp < curTimeDp) { // Molemmat curtimen vasemmalla puolella
                drawLineZ(
                    timeSetDp.toFloat(),
                    propoY + s,
                    propoEnd-width.toFloat(),
                    propoY + s,
                    paint,
                    canvas
                )
            }
            if(propoEnd > width && timeSetDp < width && timeSetDp > curTimeDp) { // Alkaa curtimen vasemmalta puolelta ja loppuu oikealle
                drawLineZ(
                    0f,
                    propoY + s,
                    propoEnd-width,
                    propoY + s,
                    paint,
                    canvas
                )
                drawLineZ(
                    timeSetDp.toFloat(),
                    propoY + s,
                    width.toFloat(),
                    propoY + s,
                    paint,
                    canvas
                )
            }
            if(propoEnd > curTimeDp && propoEnd <= width && timeSetDp < width && timeSetDp > curTimeDp) { // Alkaa ja loppuu curtimen oikealle puolelle
                drawLineZ(
                    timeSetDp.toFloat(),
                    propoY + s,
                    propoEnd,
                    propoY + s,
                    paint,
                    canvas
                )
            }

        }

    }


    // when ACTION_DOWN start touch according to the x,y values
    private fun startTouch(x: Float, y: Float) {

        if(zoomMAX==true){
            zoomMAX=false
        }
        else {

            if (zoom == 0) timeSet = (x* dpToTime).roundToLong() // timeSetDp = x

            timer_a = object : CountDownTimer(triggeringLength.toLong(), oneStep.toLong()) {
                override fun onTick(millisUntilFinished: Long) {
                    //context.toast("seconds remaining: " + millisUntilFinished / 1000)
                    zoom += 6
                }

                override fun onFinish() {
                    context.toast("MAX ZOOM")
                    zoomMAX = true
                }
            }.start()
        }
    }

    // when ACTION_MOVE move touch according to the x,y values
    private fun moveTouch(x: Float, y: Float) {

        val oneWeek = timeOnWeekEnd - timeOnWeekStart

        if(timeSetDp >= curTimeDp)
            timeSet = timeOnWeekStart + (timeSetDp*dpToTime).toLong()
        else
            timeSet = timeOnWeekStart + oneWeek + (timeSetDp*dpToTime).toLong()

        val sdf_set = SimpleDateFormat("HH:mm dd")
        var set = sdf_set.format(timeSet)
        val tv = getRootView().findViewById<TextView>(R.id.text_timeSet)
        if (tv != null) {
            tv.text=set
        }

        if(zoom !=0 && !zoomMAX) {
            timeSetDp += x-timeSetDp
            lastY = y
        }

        if(zoomMAX){
            timeSetDp += (x-timeSetDp) / zoom
        }

    }

    fun clearCanvas() {
        invalidate()
    }

    // when ACTION_UP stop touch
    private fun upTouch() {
        timer_a.cancel()

        if(zoomMAX==false){
            zoom = 0
            zoomMAX = false
            siirto = 0f
        }
    }

    //override the onTouchEvent
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTouch(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                moveTouch(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                upTouch()
                invalidate()
            }
        }
        return true
    }

    companion object {
        private const val TOLERANCE = 10f
        private lateinit var timer_a: CountDownTimer
        var zoom = 0
        var siirto: Float = 0f
        var zoomMAX: Boolean = false

        val triggeringLength=3000
        val oneStep = 10

        var lastY: Float=0f
    }

    init {
    }
}
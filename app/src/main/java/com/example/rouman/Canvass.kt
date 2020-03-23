// Globaalit muuttujat
// getviewid alaluokassa
// tietokannan luonti
// Tietueen luonti
// onDraw


package com.example.rouman;

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.rouman.MainActivity.Companion.timeSetDp
import com.example.rouman.MainActivity.Companion.timeSet
import com.example.rouman.MainActivity.Companion.timeOnWeekStart
import com.example.rouman.MainActivity.Companion.timeOnWeekEnd
import com.example.rouman.MainActivity.Companion.timeToDp
import com.example.rouman.MainActivity.Companion.curTime
import com.example.rouman.MainActivity.Companion.dpToTime
import com.example.rouman.MainActivity.Companion.propoY
import com.example.rouman.MainActivity.Companion.propoStatus
import com.example.rouman.MainActivity.Companion.cEventList
import com.example.rouman.MainActivity.Companion.curTimeDp
import kotlinx.android.synthetic.main.listview_item.view.*
import java.text.SimpleDateFormat


class Canvass(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
//    private val mPath: Path
//    private val mPaint: Paint
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
        // Draw setting line
        paint.setARGB(255, 255, 0, 0)
        paint.setStrokeWidth(14f)
        canvas.drawLine(timeSetDp.toFloat(), 0f, timeSetDp.toFloat(), height, paint)

        ///////////////////////////////////////////////
        // Draw current timeline

        val pros = ((curTime-timeOnWeekStart)/ (timeOnWeekEnd - timeOnWeekStart).toFloat())

        timeToDp =   width.toFloat() /(timeOnWeekEnd- timeOnWeekStart).toFloat()
        dpToTime = 1/timeToDp

        paint.setARGB(255, 0, 0, 0)
        paint.setStrokeWidth(4f)

        curTimeDp = (curTime-timeOnWeekStart) * timeToDp

        canvas.drawLine(curTimeDp, 0f, curTimeDp, height, paint)


        ////////////////////////////////////////////////////
        // Draw relay program line
        val b_plat = getRootView().findViewById<Button>(R.id.button12)
        if (b_plat != null){

            var endXTime = curTime + timeOnWeekEnd - timeOnWeekStart
            var endX= endXTime / dpToTime //* timeToDp
            endX =curTimeDp+width

            val plat_y  = b_plat.getTop().toFloat() // top.toFloat()
            var startX = 0f
            for(event in cEventList) {
                if(event.relay == "PLAT") {
                    if (event.setting == "0")
                        paint.setARGB(255, 0, 0, 0)
                    if (event.setting == "1")
                        paint.setARGB(255, 255, 0, 0)
                    if (event.setting == "2")
                        paint.setARGB(255, 0, 0, 255)
                    paint.setStrokeWidth(14f)

                    var eventTime = event.time!!.toLong()
                    var startXTime = eventTime
                    startX = (eventTime - timeOnWeekStart) * timeToDp

                    if (startXTime < timeOnWeekEnd && endX > curTimeDp && endX <= width){ // Jos molemmat on välillä curtime - width
                        if (startX < curTimeDp) {
                            startX = curTimeDp // viimeinen voi alkaa ennen curtime
                        }
                        if (endX > curTimeDp) { // Piirretään vain jos loppupää on canvasin alueella
                            canvas.drawLine(startX, plat_y, endX, plat_y, paint)
                            endX = startX // Seuraavanloppu on tämän alku
                        }
                    }
                    if (startXTime < timeOnWeekEnd  &&  endX>width) { // Eli jos on taitekohta pitää piirtää kahdessa välissä
                        // Näitä pitäs olla vain yks
                        canvas.drawLine(0f, plat_y, endX-width, plat_y, paint)
                        endX = width.toFloat() // jatkopala loppuu canvasin oikeaan reunaan
                        canvas.drawLine(startX, plat_y, width.toFloat(), plat_y, paint)
                        endX = startX // Seuraava loppuu tähän
                    }
                    if (startXTime > timeOnWeekEnd && endX > width ) { // Jos on alku ja loppu width:n jälkeen, eli välillä 0-curtime
                        startX = startX - width
                        canvas.drawLine(startX, plat_y, endX-width, plat_y, paint)
                        endX = startX // Seuraavanloppu on tämän alku
                    }


                    // If one after last "LAST" element?....piirrä curtimeDp ->
                }
            }
//            canvas.drawLine(startX, plat_y, endX, plat_y, paint)
        }

        /////////////////////////////////////////////////////////////
        // Draw proposal
        val b_varv = getRootView().findViewById<Button>(R.id.button13)
        if (b_varv != null){

            /////////////////////////////////////////////////////////7
            // Endpoint for porposal
            var propoEnd = curTimeDp + width
            for(event in cEventList) {
                if(event.relay == "PLAT") {
                    if(timeSet<event.time!! ) // Etsitään asetettuun aikaan nähden seuraava eventti
                        propoEnd = (event.time!! - timeOnWeekStart) * timeToDp
                }
            }
            //val plat_y  = b_varv.getTop().toFloat() // top.toFloat()
            if(propoStatus != 0) {
                if (propoStatus == 1)
                    paint.setARGB(255, 255, 0, 0)
                if (propoStatus == 2)
                    paint.setARGB(255, 0, 0, 255)
                if (propoStatus == 3)
                    paint.setARGB(255, 0, 0, 0)
                paint.setStrokeWidth(14f)

                if(propoEnd <= timeOnWeekEnd* timeToDp && timeSetDp < curTimeDp) { // Molemmat curtimen vasemmalla puolella
                    canvas.drawLine(
                        timeSetDp.toFloat(),
                        propoY + 14,
                        propoEnd-width.toFloat(),
                        propoY + 14,
                        paint
                    )
                }
                if(propoEnd > width && timeSetDp < width && timeSetDp > curTimeDp) { // Alkaa curtimen vasemmalta puolelta ja loppuu oikealle
                    canvas.drawLine(
                        0f,
                        propoY + 14,
                        propoEnd-width,
                        propoY + 14,
                        paint
                    )
                    canvas.drawLine(
                        timeSetDp.toFloat(),
                        propoY + 14,
                        width.toFloat(),
                        propoY + 14,
                        paint
                    )
                }
                if(propoEnd > curTimeDp && propoEnd <= width && timeSetDp < width && timeSetDp > curTimeDp) { // Alkaa ja loppuu curtimen oikealle puolelle
                    canvas.drawLine(
                        timeSetDp.toFloat(),
                        propoY + 14,
                        propoEnd,
                        propoY + 14,
                        paint
                    )
                }

            }
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private fun startTouch(x: Float, y: Float) {
//        mPath.moveTo(x, y)
//        mX = x
//        mY = y

        timeSetDp = x
    }

    // when ACTION_MOVE move touch according to the x,y values
    private fun moveTouch(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            // Need to check later if tolerance can be used to improve usaiblity in slow motion
            timeSetDp=x.toFloat()
            val oneWeek = timeOnWeekEnd - timeOnWeekStart
            if(timeSetDp >= curTimeDp)
                timeSet = timeOnWeekStart + (timeSetDp*dpToTime).toLong()
            else
                timeSet = timeOnWeekStart + oneWeek + (timeSetDp*dpToTime).toLong()

            val sdf_set = SimpleDateFormat("HH:mm dd")
            var set = sdf_set.format(timeSet)
            val tv = getRootView().findViewById<TextView>(R.id.text_timeSet)
            if (tv != null)
            {tv.text=set}
        }
    }

    fun clearCanvas() {
        //mPath.reset()
        invalidate()
    }

    // when ACTION_UP stop touch
    private fun upTouch() {
//        mPath.lineTo(mX, mY)
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
        private const val TOLERANCE = 1f
    }

    init {
        // we set a new Path
        //mPath = Path()

        // and we set a new Paint with the desired attributes
        /*
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = 4f
        */
    }
}
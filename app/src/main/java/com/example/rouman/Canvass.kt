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
import com.example.rouman.MainActivity.Companion.deltaTime
import com.example.rouman.MainActivity.Companion.globalDpSet
import com.example.rouman.MainActivity.Companion.globalTimeSet
import com.example.rouman.MainActivity.Companion.timeOnWeekStart
import com.example.rouman.MainActivity.Companion.timeOnWeekEnd
import com.example.rouman.MainActivity.Companion.timeToDp
import com.example.rouman.MainActivity.Companion.curTime
import com.example.rouman.MainActivity.Companion.dpToTime
import com.example.rouman.MainActivity.Companion.propoY
import com.example.rouman.MainActivity.Companion.propoStatus
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

        ////////////////////////////////////////////////////
        // Draw relay program line
        val b_plat = getRootView().findViewById<Button>(R.id.button12)
        if (b_plat != null){

            val plat_y  = b_plat.getTop().toFloat() // top.toFloat()
/*
            paint.setARGB(255, 0, 0, 0)
            paint.setStrokeWidth(14f)
            canvas.drawLine(0f, plat_y, width.toFloat(), plat_y, paint)

            paint.setARGB(255, 0, 0, 0)
//            canvas.drawLine(0F, plat_y+14, globalDpSet, plat_y+14, paint)
            paint.setARGB(255, 0, 255, 255)
            canvas.drawLine(globalDpSet, plat_y+14, width.toFloat(), plat_y+14, paint)
*/
        }

        /////////////////////////////////////////////////////////////
        // Draw proposal
        val b_varv = getRootView().findViewById<Button>(R.id.button13)
        if (b_varv != null){

            //val plat_y  = b_varv.getTop().toFloat() // top.toFloat()

            if(propoStatus==1)
                paint.setARGB(255, 255, 0, 0)
            if(propoStatus==2)
                paint.setARGB(255, 0, 0, 255)
            if(propoStatus==3)
                paint.setARGB(255, 0, 255, 0)
            paint.setStrokeWidth(14f)
            canvas.drawLine(globalDpSet.toFloat(), propoY+14, width.toFloat(), propoY+14, paint)

        }


        // Draw setting line
        paint.setARGB(255, 255, 0, 0)
        paint.setStrokeWidth(14f)
        canvas.drawLine(globalDpSet.toFloat(), 0f, globalDpSet.toFloat(), height, paint)

        ///////////////////////////////////////////////
        // Draw current timeline
//        val systemTime = System.currentTimeMillis()
//        val pros = ((systemTime-timeOnWeekStart).toFloat()/ (timeOnWeekEnd - timeOnWeekStart).toFloat())
//        var currentTimeDp =  pros * width.toFloat()
//        timeToDp =  currentTimeDp / (System.currentTimeMillis() - timeOnWeekStart).toFloat()
//        dpToTime =  (System.currentTimeMillis() - timeOnWeekStart) / currentTimeDp

        val systemTime = curTime
        val pros = ((systemTime-timeOnWeekStart).toFloat()/ (timeOnWeekEnd - timeOnWeekStart).toFloat())
        var currentTime =  pros * width.toFloat()
        timeToDp =  currentTime / (curTime - timeOnWeekStart).toFloat()
        dpToTime =  (curTime - timeOnWeekStart) / currentTime

        paint.setARGB(255, 0, 0, 0)
        paint.setStrokeWidth(4f)
        var currentTimeDp = (curTime-timeOnWeekStart) * timeToDp
        canvas.drawLine(currentTimeDp, 0f, currentTimeDp, height, paint)



    }

    // when ACTION_DOWN start touch according to the x,y values
    private fun startTouch(x: Float, y: Float) {
//        mPath.moveTo(x, y)
//        mX = x
//        mY = y


        globalDpSet = x

    }

    // when ACTION_MOVE move touch according to the x,y values
    private fun moveTouch(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            // Need to check later if tolerance can be used to improve usaiblity in slow motion
            globalDpSet=x
            globalTimeSet = timeOnWeekStart + (globalDpSet*dpToTime).toLong()
            val sdf_set = SimpleDateFormat("HH:mm dd")
            var set = sdf_set.format(globalTimeSet)
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
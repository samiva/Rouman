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
import android.widget.TextView
import com.example.rouman.MainActivity.Companion.globalTimeSet



class Canvass(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mPath: Path
    private val mPaint: Paint
    private var mX = 0f
    private var mY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
    }

    // override onDraw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = getWidth()
        val height = getHeight().toFloat()

        val paint = Paint()
        paint.setARGB(255, 255, 0, 0)
        paint.setStrokeWidth(14f)
        canvas.drawLine(0f, 30f, width.toFloat(), 30f, paint)
        paint.setARGB(255, 0, 255, 0)
        canvas.drawLine(100f, 60f, width.toFloat(), 60f, paint)

        // Draw setting line
        paint.setARGB(255, 255, 0, 0)
        paint.setStrokeWidth(14f)
        canvas.drawLine(globalTimeSet.toFloat(), 0f, globalTimeSet.toFloat(), height, paint)


        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint)



    }

    // when ACTION_DOWN start touch according to the x,y values
    private fun startTouch(x: Float, y: Float) {
//        mPath.moveTo(x, y)
//        mX = x
//        mY = y


        globalTimeSet = x.toInt()

    }

    // when ACTION_MOVE move touch according to the x,y values
    private fun moveTouch(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
  //          mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
  //          mX = x
  //          mY = y

            globalTimeSet=x.toInt()
            val tv = getRootView().findViewById<TextView>(R.id.text_timeSet)
            if (tv != null)
            {tv.text=globalTimeSet.toString()}


        }
    }

    fun clearCanvas() {
        mPath.reset()
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
        mPath = Path()

        // and we set a new Paint with the desired attributes
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = 4f
    }
}
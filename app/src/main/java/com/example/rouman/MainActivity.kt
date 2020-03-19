package com.example.rouman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.internals.AnkoInternals.getContext
import android.widget.TextView as TextView1


class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val customCanvas =
//            findViewById<View>(R.id.canvasView) as Canvass
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
companion object {
    var globalTimeSet=10
}

}

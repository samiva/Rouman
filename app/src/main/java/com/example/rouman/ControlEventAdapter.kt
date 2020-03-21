package com.example.rouman

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.listview_item.view.*

class ControlEventAdapter(context: Context, private val list: List<ControlEvent>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

// UId, time, relay, status
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row = inflater.inflate(R.layout.listview_item, parent,false)

        val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy")
        sdf.timeZone = TimeZone.getDefault()

        val timeText = list[position].time
        val readableTime =sdf.format(timeText)
        row.time.text = readableTime

        row.relay.text = list[position].relay
        row.status.text = list[position].setting

        return row
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}
package com.xilli.stealthnet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xilli.stealthnet.R
import com.xilli.stealthnet.data.DataItemFree

class SearchView_Free_Adapter(private val context: Context, private val dataList: List<DataItemFree>) :
    RecyclerView.Adapter<SearchView_Free_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_free_server, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        holder.flagImageView.setImageResource(data.flagimageUrl)
        holder.flagNameTextView.text = data.title
        holder.vpnIpTextView.text = data.IPdescription
        holder.signalview.setImageResource(data.signal)
        holder.radioButton.isChecked = data.radioButtonChecked
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagImageView: ImageView = itemView.findViewById(R.id.imageView200)
        val flagNameTextView: TextView = itemView.findViewById(R.id.flag_name2)
        val vpnIpTextView: TextView = itemView.findViewById(R.id.vpn_ip2)
        val signalview: ImageView =itemView.findViewById(R.id.signalgreen2)
        val radioButton: RadioButton =itemView.findViewById(R.id.radio2)
        init {
            radioButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val data = dataList[position]
                    data.radioButtonChecked = radioButton.isChecked
                    notifyItemChanged(position)
                }
            }
        }
    }
}
package com.xilli.stealthnet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.xilli.stealthnet.R
import com.xilli.stealthnet.helper.Countries

class SearchView_Premium_Adapter(
    private val context: Context,
    private val dataList: List<Countries>
) : RecyclerView.Adapter<SearchView_Premium_Adapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    var datalist = ArrayList<Countries>()
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
    }
    fun setData(servers: List<Countries?>) {
        datalist.clear()
        datalist.addAll(servers.filterNotNull()) // Filter out null elements if any
        notifyDataSetChanged()
        selectedPosition = RecyclerView.NO_POSITION
    }
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun resetSelection() {
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_premier_server, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        Picasso.get().load(data.flagUrl).into(holder.flagImageView)
        holder.flagNameTextView.text = data.country
        holder.signalImageView.setImageResource(data.signal) // Use the signal property
        holder.crownImageView.setImageResource(data.crown)   // Use the crown property


        holder.constraintLayout.setBackgroundResource(
            if (position == selectedPosition) R.drawable.selector_background
            else R.drawable.background_black_card
        )

        holder.constraintLayout.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagImageView: ImageView = itemView.findViewById(R.id.imageView)
        val flagNameTextView: TextView = itemView.findViewById(R.id.flag_name)
        val vpnIpTextView: TextView = itemView.findViewById(R.id.vpn_ip)
        val signalImageView: ImageView = itemView.findViewById(R.id.signalgreen)
        val crownImageView: ImageView = itemView.findViewById(R.id.radio)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutpremium)
    }
}

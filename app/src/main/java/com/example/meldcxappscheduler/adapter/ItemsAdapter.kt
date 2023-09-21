package com.example.meldcxappscheduler.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.meldcxappscheduler.data.AppInfo
import com.example.meldcxappscheduler.data.AppScheduleStore
import com.example.meldcxappscheduler.databinding.ItemCardviewBinding
import com.example.meldcxappscheduler.scheduler.AppScheduler
import com.example.meldcxappscheduler.viewModel.MainViewModel

// Created by @author Moniruzzaman on 18/9/23. github: filelucker

class ItemsAdapter(
    private val mainViewModel: MainViewModel,
    private var mList: ArrayList<AppInfo>,
    private val listener: OnClickListener
) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = ItemCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageDrawable(ItemsViewModel.icon)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.appName
        holder.textViewSchadule.text = ItemsViewModel.scheduleTime
        if (ItemsViewModel.scheduleTime.equals("")) {
            holder.imageViewDelete.visibility = View.GONE
        } else {
            holder.imageViewDelete.visibility = View.VISIBLE
        }

        holder.imageViewDelete.setOnClickListener {
            showDeleteDialogue(it, mList[position])
        }
    }

    private fun showDeleteDialogue(view: View, appInfo: AppInfo) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Delete!!!")
        builder.setMessage("Are you sure to delete schaduled time?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            mainViewModel.delete(appInfo.id)
            this.mList[this.mList.indexOf(appInfo)] =
                AppInfo(appInfo.id, appInfo.appName, appInfo.packageName, appInfo.icon, "")
            Toast.makeText(view.context, "Schadule Deleted", Toast.LENGTH_LONG).show()
            val appScheduler = AppScheduler(view.context)
            appScheduler.cancelScheduledApp(appInfo.packageName)
            notifyDataSetChanged()
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: ItemCardviewBinding) : RecyclerView.ViewHolder(itemView.root),
        View.OnClickListener {
        val imageView: ImageView = itemView.imageview

        val textView: TextView = itemView.textView
        val textViewSchadule: TextView = itemView.textView2

        init {
            itemView.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(position)
            }
        }

        val imageViewDelete: ImageView = itemView.imageView2
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetView(newList: ArrayList<AppInfo>) {
        this.mList = newList
        this.notifyDataSetChanged()
    }


}
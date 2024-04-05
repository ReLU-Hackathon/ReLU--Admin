package com.lapperapp.laper.ui.NewDashboard.NewRequest

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laper.laperadmin.R
import com.laper.laperadmin.RequestViewActivity
import com.laper.laperadmin.TimeAgo
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class NewRequestAdapter(private val mList: List<NewRequestSentModel>) :
    RecyclerView.Adapter<NewRequestAdapter.ViewHolder>() {
    var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_request_item, parent, false)
        return ViewHolder(view)
    }

    fun str(ps: String): String {
        if (ps.length > 80) {
            return ps.subSequence(0, 80).toString() + "..."
        } else {
            return ps
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val context = holder.itemView.context

        val model = mList[position]

        val timeAgo = TimeAgo()
        val reqSentDateInMillis = model.reqSentDate.toLong()
        val currentDate = timeAgo.getTimeAgo(Date(reqSentDateInMillis), context)
//        holder.reqDate.text = currentDate
        holder.reqPs.text = str(model.ps)
        holder.reqId.text = "Task ID : "+model.id
        holder.itemView.setOnClickListener {
            val reqIntent = Intent(context, RequestViewActivity::class.java)
            reqIntent.putExtra("requestId",model.reqId)
            context.startActivity(reqIntent)
        }


    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        var reqImage: CircleImageView = itemView.findViewById(R.id.new_request_sent_image)
        var reqId: TextView = itemView.findViewById(R.id.new_request_sent_task_id)
        var reqPs: TextView = itemView.findViewById(R.id.new_request_sent_ps)
//        var reqDate: TextView = itemView.findViewById(R.id.new_request_date)
    }

}
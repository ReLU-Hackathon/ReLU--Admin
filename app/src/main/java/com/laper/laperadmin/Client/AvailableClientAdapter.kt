package com.laper.laperadmin.Client

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laper.laperadmin.AcceptedRequestActivity
import com.laper.laperadmin.Chat.ChatActivity
import com.laper.laperadmin.R
import com.laper.laperadmin.RequestViewActivity
import com.laper.laperadmin.TimeAgo
import java.util.*

class AvailableClientAdapter(private val mList: List<AvailableClientModel>) :
    RecyclerView.Adapter<AvailableClientAdapter.ViewHolder>() {
    var db = Firebase.firestore
    var userRef = db.collection("users")
    var auth = FirebaseAuth.getInstance()

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val model = mList[position]

        val timeAgo = TimeAgo()
        val currentDate = timeAgo.getTimeAgo(Date(model.reqSentDate),context)
//        holder.reqDate.text = currentDate
//        holder.reqName.text = model.expName
        holder.reqPs.text = str(model.ps)
        Glide.with(context).load(model.expImage).placeholder(R.drawable.logo).into(holder.reqImage)
        holder.itemView.setOnClickListener {
            val reqIntent = Intent(context, AcceptedRequestActivity::class.java)
            reqIntent.putExtra("requestId",model.reqId)
            context.startActivity(reqIntent)
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var reqImage: ImageView = itemView.findViewById(R.id.new_request_sent_image)
//        var reqName: TextView = itemView.findViewById(R.id.new_request_sent_name)
        var reqPs: TextView = itemView.findViewById(R.id.new_request_sent_ps)
//        var reqDate: TextView = itemView.findViewById(R.id.new_request_date)
    }

}
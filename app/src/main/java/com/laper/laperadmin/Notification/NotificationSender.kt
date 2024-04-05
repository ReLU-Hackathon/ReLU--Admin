package com.laper.laperadmin.Notification

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laper.laperadmin.service.PushNotification

class NotificationSender constructor(val context: Context, val userId: String) {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    var userRef = db.collection("users")

    fun sendNotification(ps: String, title: String,type:String="0"){
        val id = System.currentTimeMillis().toString()
        val hash = hashMapOf(
            "id" to id,
            "type" to type,
            "title" to title,
            "text" to ps,
            "userId" to auth.uid,
            "time" to System.currentTimeMillis()
        )

        userRef.document(userId)
            .collection("notification")
            .document(id)
            .set(hash)

        userRef.document(userId)
            .update("notificationCount", FieldValue.increment(1))
        userRef.document(userId)
            .update("dashboardNotification", true)

        val pn = PushNotification(context)
        pn.sendNotification(userId, title, ps , type)
    }
}
package com.laper.laperadmin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laper.laperadmin.Chat.ChatActivity
import com.laper.laperadmin.Notification.NotificationSender
import com.laper.laperadmin.service.PushNotification
import de.hdodenhof.circleimageview.CircleImageView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AcceptedRequestActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    var db = Firebase.firestore
    var userRef = db.collection("users")
    var expertRef = db.collection("experts")
    var techRef = db.collection("tech")

    private lateinit var acceptBtn: CardView
    private lateinit var taskCompleted:CardView
    private lateinit var userImage: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userDate: TextView
    private lateinit var problemStatement: TextView
    private lateinit var titles: TextView
    private lateinit var requestId: String
    private lateinit var techTitles: String
    private lateinit var clientId: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accepted_request)
        userImage = findViewById(R.id.accepted_request_user_image)
        userName = findViewById(R.id.accepted_request_user_name)
        userDate = findViewById(R.id.accepted_request_request_date)
        problemStatement = findViewById(R.id.accepted_request_problem_statement)
        titles = findViewById(R.id.accepted_request_tech_title)
        acceptBtn = findViewById(R.id.accepted_request_accept_btn)
        taskCompleted = findViewById(R.id.accepted_request_task_completed_btn)
        requestId = intent.getStringExtra("requestId").toString()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Task Completed")
        builder.setMessage("Are you sure you have resolved the issue?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            taskCompleted()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }


        taskCompleted.setOnClickListener {
            builder.show()
        }

        fetchDetails()
    }

    fun taskCompleted(){
        expertRef.document(auth.uid.toString())
            .collection("requests")
            .document(requestId).get().addOnSuccessListener { doc->
                val resDoc = doc.data
                val addDoc = hashMapOf(
                    "taskCompleteTime" to System.currentTimeMillis(),
                    "problemSolved" to true
                )
                if (resDoc != null) {
                    expertRef.document(auth.uid.toString())
                        .collection("taskComplete")
                        .document(requestId)
                        .set(resDoc+addDoc)
                    userRef.document(clientId)
                        .collection("taskComplete")
                        .document(requestId)
                        .set(resDoc+addDoc)
                    expertRef.document(auth.uid.toString())
                        .collection("requests").document(requestId).delete()
                    userRef.document(clientId)
                        .collection("availableExpert").document(requestId).delete()
                    expertRef.document(auth.uid.toString())
                        .collection("taskComplete")
                        .document(requestId).set(resDoc+addDoc)
                    val ns = NotificationSender(baseContext,clientId)
                    ns.sendNotification(resDoc.get("problemStatement").toString(),"Task is completed!!","2")
                    Toast.makeText(baseContext,"Task Completed",Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun fetchTech(id: String) {
        techRef.document(id.trim())
            .get().addOnSuccessListener { doc ->
                val title = doc.getString("name") as String
                techTitles += "# $title   "
                titles.text = techTitles
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchDetails() {
        techTitles = ""
        expertRef.document(auth.uid.toString())
            .collection("requests")
            .document(requestId).get().addOnSuccessListener { doc->
                if (doc.exists()){
                    val ps = doc.getString("problemStatement")
                    val reqTime = doc.getLong("requestTime") as Long
                    val reqTech = doc.get("requiredTech") as ArrayList<String>
                    clientId = doc.getString("clientId") as String
                    problemStatement.text = ps
                    for (id in reqTech) {
                        fetchTech(id)
                    }
                    acceptBtn.setOnClickListener {
                        val intent = Intent(baseContext, ChatActivity::class.java)
                        intent.putExtra("userId", clientId)
                        startActivity(intent)
                    }
                    userRef.document(clientId)
                        .get().addOnSuccessListener { doc1 ->
                            val name = doc1.getString("username").toString()
                            val imageUrl = doc1.getString("userImageUrl").toString()
                            userName.text = name
                            Glide.with(baseContext).load(imageUrl).placeholder(R.drawable.logo)
                                .into(userImage)
                        }
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    val instant = Instant.ofEpochMilli(reqTime)
                    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    userDate.text = formatter.format(date).toString()

                }
            }
    }



}
package com.laper.laperadmin.Chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laper.laperadmin.R
import com.laper.laperadmin.service.PushNotification
import com.lapperapp.laper.Chat.ChatAdapter
import com.lapperapp.laper.Chat.ChatModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    var userRef = db.collection("users")

    val database = Firebase.database
    val chatRef = database.getReference("chats")
    val userChatListRef = database.getReference("userchatlist")
    val messageIdList = arrayListOf<String>()

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatModel: ArrayList<ChatModel>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var msgEdit: EditText
    private lateinit var sendBtn: ImageView
    private lateinit var receiverUserId: String
    private lateinit var senderUserId: String
    private lateinit var userImage: CircleImageView
    private lateinit var userName: TextView
    private lateinit var appBar: AppBarLayout
    private lateinit var taskDone: ImageView

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        userImage = findViewById(R.id.chat_user_image_app_bar)
        userName = findViewById(R.id.chat_user_name_app_bar)
        msgEdit = findViewById(R.id.text_message_chat)
        appBar = findViewById(R.id.chat_app_bar_layout)
        sendBtn = findViewById(R.id.send_msg_btn_chat)
        taskDone = findViewById(R.id.user_chat_done)

        chatRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        chatModel = ArrayList()

        receiverUserId = intent.getStringExtra("userId").toString()
        senderUserId = "expert"
        chatAdapter = ChatAdapter(chatModel, receiverUserId)
        chatRecyclerView.adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()
        Log.d("chat userID ", receiverUserId)

        sendBtn.setOnClickListener {
            sendMessage()
        }

        fetchMessages()
        addUser()
        fetchUser()

    }

    fun fetchUser() {
        userRef.document(receiverUserId)
            .get().addOnSuccessListener { doc ->
                val username = doc.getString("username")
                val userimageUrl = doc.getString("userImageUrl")
                userName.text = username
                Glide.with(baseContext).load(userimageUrl).into(userImage)
            }
    }


    fun addUser() {
        val userChat = hashMapOf(
            "lastChatDate" to System.currentTimeMillis()
        )

        userChatListRef.child(auth.uid.toString())
            .child(receiverUserId)
            .setValue(userChat)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchMessages() {
        chatRef.child(senderUserId)
            .child(receiverUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        if (child.exists()){
                            if (!messageIdList.contains(child.key.toString())) {
                                val message = child.child("text").value.toString()
                                val sendDate = child.child("sentDate").value as Long
                                val receiverId = child.child("receiverId").value.toString()
                                val type = child.child("type").value as Long
                                val read = child.child("read").value as Boolean
                                chatModel.add(ChatModel(message, sendDate, receiverId, type,read))
                                chatAdapter.notifyDataSetChanged()
                                messageIdList.add(child.key.toString())
                            }
                        }
                    }
                    chatRecyclerView.layoutManager?.scrollToPosition(snapshot.children.count() - 1)


                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun sendMessage() {
        val msg = msgEdit.text.toString().trim()

        if (msg.isEmpty()) {
            return
        }

        val childId = chatRef.push().key.toString()

        val senderHashMap = hashMapOf(
            "sentDate" to System.currentTimeMillis(),
            "receiverId" to receiverUserId,
            "text" to msg,
            "type" to 0,
            "read" to false
        )
        chatRef.child(senderUserId)
            .child(receiverUserId)
            .child(childId).setValue(senderHashMap)

        val receiverHashMap = hashMapOf(
            "sentDate" to System.currentTimeMillis(),
            "receiverId" to receiverUserId,
            "text" to msg,
            "type" to 0,
            "read" to false
        )
        chatRef.child(receiverUserId)
            .child(senderUserId)
            .child(childId).setValue(receiverHashMap)

        msgEdit.setText("")
        val userHash = hashMapOf(
            "lastChatDate" to System.currentTimeMillis(),
            "lastMessage" to msg
        )

        userChatListRef.child(senderUserId)
            .child(receiverUserId)
            .setValue(userHash)

        userChatListRef.child(receiverUserId)
            .child(senderUserId)
            .setValue(userHash)

        chatRecyclerView.layoutManager?.scrollToPosition(chatAdapter.itemCount - 1)

        val pn = PushNotification(baseContext)
        pn.sendNotification(receiverUserId, "New Message", msg, "2")

    }


}
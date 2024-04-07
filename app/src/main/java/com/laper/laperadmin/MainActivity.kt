package com.laper.laperadmin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.laper.laperadmin.Client.AvailableClientAdapter
import com.laper.laperadmin.Client.AvailableClientModel
import com.laper.laperadmin.Model.FilterModel
import com.laper.laperadmin.service.Constants.TAG
import com.laperapp.laper.ResponseBodyApi
import com.lapperapp.laper.ui.NewDashboard.NewRequest.NewRequestAdapter
import com.lapperapp.laper.ui.NewDashboard.NewRequest.NewRequestSentModel
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    private lateinit var reqOnlyMeReceivedRecyclerView: RecyclerView
    private lateinit var expertReqRecyclerView: RecyclerView
    private lateinit var availableClientRecyclerView: RecyclerView
    var db = Firebase.firestore
    val database = Firebase.database
    val tokenRef = database.getReference("token")
    var auth = FirebaseAuth.getInstance()
    private lateinit var reqOnlyMeReceivedModel: ArrayList<NewRequestSentModel>
    private lateinit var reqOnlyMeReceivedAdapter: NewRequestAdapter
    private lateinit var settingBtn: CircleImageView

    private lateinit var expertReqReceivedModel: ArrayList<NewRequestSentModel>
    private lateinit var expertReqReceivedAdapter: NewRequestAdapter

    private lateinit var avaClientModel: ArrayList<AvailableClientModel>
    private lateinit var avaClientAdapter: AvailableClientAdapter

    private lateinit var reqId: ArrayList<String>
    private lateinit var clientIds: ArrayList<String>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reqId = ArrayList()
        clientIds = ArrayList()


        settingBtn = findViewById(R.id.setting_btn)

        reqOnlyMeReceivedRecyclerView = findViewById(R.id.dashboard_received_request_only_me_recycler_view)
        reqOnlyMeReceivedRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        reqOnlyMeReceivedModel = ArrayList()
        reqOnlyMeReceivedAdapter = NewRequestAdapter(reqOnlyMeReceivedModel)
        reqOnlyMeReceivedRecyclerView.adapter = reqOnlyMeReceivedAdapter

        expertReqRecyclerView = findViewById(R.id.dashboard_received_request_all_expert_recycler_view)
        expertReqRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        expertReqReceivedModel = ArrayList()
        expertReqReceivedAdapter = NewRequestAdapter(expertReqReceivedModel)
        expertReqRecyclerView.adapter = expertReqReceivedAdapter

        availableClientRecyclerView = findViewById(R.id.dashboard_available_client_recycler_view)
        availableClientRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        avaClientModel = ArrayList()
        avaClientAdapter = AvailableClientAdapter(avaClientModel)
        availableClientRecyclerView.adapter = avaClientAdapter

        settingBtn.setOnClickListener {
            val intent = Intent(baseContext, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        expertReqReceivedModel.clear()
        reqOnlyMeReceivedModel.clear()
        fetchMyRequests()
        fetchAllRequest()
        fetchAvailableClient()
        setToken()
    }

    fun setToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { s ->
            val tokenhash = hashMapOf(
                "token" to s
            )
            tokenRef.child(auth.uid.toString()).setValue(tokenhash)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchAvailableClient() {
        val arr = ArrayList<String>()
        arr.add("python")
        arr.add("java")
        var expertId = "expert@gmail.com"

        val filter = FilterModel("expertId", expertId, "requestTime", lim = 6)
        ResponseBodyApi.fetchRequest(filter, onResponse = { res ->
            val req = res?.request
            if (req != null) {
                for (model in req) {
                    if(!clientIds.contains(model.requestId)){
                        if(model.status.equals("accepted")){
                            avaClientModel.add(
                                AvailableClientModel(
                                    model.requestTime.toLong(),
                                    expertId,
                                    model.requestId,
                                    "",
                                    "",
                                    model.problemStatement,
                                    ArrayList(),
                                    model.clientId)
                            )
                        }
                        clientIds.add(model.requestId)
                    }
                }
                avaClientAdapter.notifyDataSetChanged()
            }
        }, onFailure = { t ->
            Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun fetchAllRequest() {
        val arr = ArrayList<String>()
        arr.add("python")
        arr.add("java")

        val filter = FilterModel("expertId", "all", "requestTime", lim = 6)
        ResponseBodyApi.fetchRequest(filter, onResponse = { res ->
            val req = res?.request
            if (req != null) {
                for (model in req) {
                    if(model.status.equals("active")){
                        expertReqReceivedModel.add(
                            NewRequestSentModel(
                                model._id,
                                model.requestTime,
                                model.expertId,
                                model.requestId,
                                model.problemStatement,
                                arr
                            )
                        )
                    }
                }
                expertReqReceivedAdapter.notifyDataSetChanged()
            }
        }, onFailure = { t ->
            Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun fetchMyRequests() {
        val arr = ArrayList<String>()
        arr.add("python")
        arr.add("java")

        val filter = FilterModel("expertId", "expert@gmail.com", "requestTime", lim = 6)
        ResponseBodyApi.fetchRequest(filter, onResponse = { res ->
            val req = res?.request
            if (req != null) {
                for (model in req) {
                    if(model.status.equals("active")){
                        expertReqReceivedModel.add(
                            NewRequestSentModel(
                                model._id,
                                model.requestTime,
                                model.expertId,
                                model.requestId,
                                model.problemStatement,
                                arr
                            )
                        )
                    }
                }
                expertReqReceivedAdapter.notifyDataSetChanged()
            }
        }, onFailure = { t ->
            Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
        })
    }


}
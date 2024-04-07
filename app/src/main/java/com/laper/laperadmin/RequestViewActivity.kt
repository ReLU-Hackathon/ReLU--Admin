package com.laper.laperadmin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.laper.laperadmin.Model.FilterModel
import com.laper.laperadmin.Model.UpdateReqModel
import com.laperapp.laper.ResponseBodyApi
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RequestViewActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()

    private lateinit var acceptBtn: CardView
    private lateinit var cancelBtn: Button
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
        setContentView(R.layout.activity_request_view)
        userImage = findViewById(R.id.request_view_user_image)
        userName = findViewById(R.id.request_view_user_name)
        userDate = findViewById(R.id.request_view_request_date)
        problemStatement = findViewById(R.id.request_view_problem_statement)
        titles = findViewById(R.id.request_view_tech_title)
        acceptBtn = findViewById(R.id.request_view_accept_btn)
        cancelBtn = findViewById(R.id.request_view_cancel_btn)

        requestId = intent.getStringExtra("requestId").toString()

        fetchDetails()


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Accept")
        builder.setMessage("Are you sure you will able to solve this issue?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            setReqAccept()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
        }

        acceptBtn.setOnClickListener {
            builder.show()
        }



        fetchTech(requestId)


    }


    fun setReqAccept(){
        val updateModel = UpdateReqModel(requestId,"status","accepted")
        ResponseBodyApi.updateRequest(baseContext,updateModel, onResponse = {res->
            val updateModel1 = UpdateReqModel(requestId,"expertId","expert@gmail.com")
            ResponseBodyApi.updateRequest(baseContext,updateModel1, onResponse = {res->
                Toast.makeText(baseContext,"Accepted",Toast.LENGTH_SHORT).show()
            }, onFailure = {t->
                Toast.makeText(baseContext,"error"+t.message,Toast.LENGTH_SHORT).show()
                Log.d("error",t.message.toString())
            })
        }, onFailure = {t->
            Toast.makeText(baseContext,"error"+t.message,Toast.LENGTH_SHORT).show()
            Log.d("error",t.message.toString())
        })
    }


    fun fetchUser(userId:String){
        val filter = FilterModel("userId",userId,"lastActive", lim = 1)
        ResponseBodyApi.getUserResponseBody(baseContext,filter, onResponse = {res->
            val req = res?.user?.get(0)
            if (req != null) {
                userName.text = req.name
                Glide.with(baseContext).load(req.userImageUrl).into(userImage)
            }
        }, onFailure = {t->
            Log.d("error",t.message.toString())
        })
    }

    fun fetchTech(id: String) {
        val filter = FilterModel("requestId",id,"requestTime", lim = 6)
        ResponseBodyApi.fetchRequest(filter, onResponse = { res->
            val req = res?.request
            if (req != null) {
                for(model in req){
                    val timeAgo = TimeAgo()
                    val reqSentDateInMillis = model.requestTime.toLong()
                    val currentDate = timeAgo.getTimeAgo(Date(reqSentDateInMillis), baseContext)
                    problemStatement.text = model.problemStatement
                    userDate.text = currentDate
                    fetchUser(model.clientId)
                }
            }
        }, onFailure = {t->
            Toast.makeText(baseContext,t.message,Toast.LENGTH_SHORT).show()
        })
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchDetails() {
        techTitles = ""
    }



}
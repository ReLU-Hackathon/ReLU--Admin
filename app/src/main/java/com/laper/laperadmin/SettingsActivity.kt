package com.laper.laperadmin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.laperapp.laper.ResponseBodyApi

class SettingsActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    var expertRef = db.collection("experts")
    private lateinit var update:Button
    private lateinit var email:TextView
    private lateinit var name:EditText
    private lateinit var desc:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        update = findViewById(R.id.setting_update)
        email = findViewById(R.id.setting_email)
        name = findViewById(R.id.setting_name)
        desc = findViewById(R.id.setting_desc)

        update.setOnClickListener{
            update()
        }
        fetchData()
    }

    fun fetchData(){
        ResponseBodyApi.getExpertResponseBody(baseContext,
            onResponse = { json ->
                val expert = json?.user
                if (expert != null) {
                    name.setText(expert.name)
                    desc.setText(expert.desc)
                    email.setText(expert.email)
                }
            },
            onFailure = { t ->
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun update(){
        val hash = hashMapOf(
            "username" to name.text.toString().trim(),
            "desc" to desc.text.toString().trim(),
        )
        expertRef.document(auth.uid.toString())
            .update(hash as Map<String, Any>)
        Toast.makeText(baseContext,"updated!",Toast.LENGTH_SHORT).show()
    }

}
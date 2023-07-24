package com.example.firebasepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasepractice.adapters.ChatAdapter
import com.example.firebasepractice.databinding.ActivityGroupChatBinding
import com.example.firebasepractice.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class GroupChatActivity : AppCompatActivity() {
    lateinit var database:FirebaseDatabase
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityGroupChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupChatBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backFromChatD.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        database = Firebase.database
        auth = Firebase.auth
        var messageModel:ArrayList<MessageModel> = ArrayList()

        val senderId:String = auth.uid.toString()

        binding.userNameChat.text = "EveryOne Group"
        val adapter:ChatAdapter = ChatAdapter(messageModel,this)
        binding.chatRecycleView.adapter= adapter

        val linearLayoutManager:LinearLayoutManager = LinearLayoutManager(this)
        binding.chatRecycleView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true
        database.getReference().child("Group Chat").addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                messageModel.clear()
                for(snapshot1 in snapshot.children){
                    val mModel :MessageModel = snapshot1.getValue(MessageModel::class.java)!!
                    messageModel.add(mModel)
                    linearLayoutManager.scrollToPosition(messageModel.size-1)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.send.setOnClickListener {
            val message = binding.etMessage.text.toString()
            val model:MessageModel = MessageModel(senderId,message)
            binding.etMessage.text.clear()
            model.time = Date().time

            database.getReference().child("Group Chat").push().setValue(model).addOnSuccessListener {

            }

        }

    }
}
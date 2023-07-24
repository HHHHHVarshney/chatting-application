package com.example.firebasepractice

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasepractice.adapters.ChatAdapter
import com.example.firebasepractice.databinding.ActivityChatDetailBinding
import com.example.firebasepractice.databinding.ActivityMainBinding
import com.example.firebasepractice.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailBinding // Use the generated binding class instead of the activity class
    lateinit var database: FirebaseDatabase
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater) // Inflate the activity layout using the generated binding class

        setContentView(binding.root)
        supportActionBar?.hide()
        database = Firebase.database
        auth = Firebase.auth

        val senderId = auth.uid.toString()
        val receiverId = intent.getStringExtra("userId")
        val profilePic = intent.getStringExtra("profilePic")
        val username = intent.getStringExtra("username")

        binding.userNameChat.text = username
        if (!profilePic.isNullOrEmpty()) {
            Picasso.get().load(profilePic).placeholder(R.drawable.person_unk).into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.person_unk)
        }

        binding.backFromChatD.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        var messageModel:ArrayList<MessageModel> = ArrayList()
        val chatAdapter:ChatAdapter = ChatAdapter(messageModel, this, receiverId.toString())
        binding.chatRecycleView.adapter = chatAdapter
        val layoutManager:LinearLayoutManager = LinearLayoutManager(this)
        layoutManager.setStackFromEnd(true);
        binding.chatRecycleView.layoutManager = layoutManager


        val senderRoom:String = senderId + receiverId
        val receiverRoom:String = receiverId + senderId


        database.getReference().child("Chats").child(senderRoom).addValueEventListener(object:
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                messageModel.clear()
                for(snapshot1 in snapshot.children){
                    val mModel :MessageModel = snapshot1.getValue(MessageModel::class.java)!!
                    val messageId = snapshot1.key // This retrieves the messageId
                    mModel.messageId = messageId!!
                    messageModel.add(mModel)
                    layoutManager.scrollToPosition(messageModel.size - 1)
                }

                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })


        binding.send.setOnClickListener {
            val message:String = binding.etMessage.text.toString()
            val messageModel:MessageModel = MessageModel(senderId,message)
            messageModel.time = Date().time
            binding.etMessage.text.clear()

            database.getReference().child("Chats").child(senderRoom).push().setValue(messageModel).addOnSuccessListener {
                database.getReference().child("Chats").child(receiverRoom).push().setValue(messageModel).addOnSuccessListener {

                }
            }
        }

    }
}
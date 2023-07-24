package com.example.firebasepractice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasepractice.adapters.recycleAdapterChat
import com.example.firebasepractice.databinding.FragmentChatBinding
import com.example.firebasepractice.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    var list: ArrayList<Users> = ArrayList()
    lateinit var database:FirebaseDatabase
    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentChatBinding.inflate(inflater, container, false)

        val adapter:recycleAdapterChat = recycleAdapterChat(list, context)
        binding.chatRecycle.adapter = adapter
        database = Firebase.database
        auth = Firebase.auth

        val linearLayoutManager:LinearLayoutManager = LinearLayoutManager(context)
        binding.chatRecycle.layoutManager = linearLayoutManager
         database.getReference().child("Users").addValueEventListener(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 list.clear()
                 for(dataSnapshot in snapshot.children){
                     val user: Users = dataSnapshot.getValue(Users::class.java)!!
                     user.uId = dataSnapshot.key.toString()
                     if(user.uId.toString() != auth.currentUser?.uid.toString()) {
                         list.add(user)
                     }
                 }
                 adapter.notifyDataSetChanged()
             }

             override fun onCancelled(error: DatabaseError) {

             }

         })

        return binding.root
    }
}
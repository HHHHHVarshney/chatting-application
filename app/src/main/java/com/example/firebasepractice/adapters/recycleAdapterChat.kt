package com.example.firebasepractice.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepractice.ChatDetailActivity
import com.example.firebasepractice.R
import com.example.firebasepractice.models.MessageModel
import com.example.firebasepractice.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class recycleAdapterChat : RecyclerView.Adapter<recycleAdapterChat.viewHolder>{

    val list:ArrayList<Users>
    val context:Context?
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth

    constructor(list: ArrayList<Users>, context: Context?) {
        this.list = list
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_recycle_chat,parent,false)


        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        database = Firebase.database
        auth = Firebase.auth
        val users:Users = list[position]
        if (!users.profilePic.isNullOrEmpty()) {
            Picasso.get().load(users.profilePic).placeholder(R.drawable.person_unk).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.person_unk)
        }
        holder.username.text = users.username

        database.getReference().child("Chats").child(auth.uid + users.uId).orderByChild("time").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                   if(snapshot.hasChildren()){
                       for(snapshot1 in snapshot.children){
                           holder.lastMessage.text = snapshot1.child("message").getValue().toString()
                       }
                   }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatDetailActivity::class.java)
            intent.putExtra("userId",users.uId)
            intent.putExtra("profilePic",users.profilePic)
            intent.putExtra("username",users.username)
            context?.startActivity(intent)
        }
    }


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.profile_image)
        val username = itemView.findViewById<TextView>(R.id.userNameChat)
        val lastMessage = itemView.findViewById<TextView>(R.id.lastMessage)
    }
}
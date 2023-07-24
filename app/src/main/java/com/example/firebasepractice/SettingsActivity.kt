@file:Suppress("DEPRECATION")

package com.example.firebasepractice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.firebasepractice.models.Users
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.example.firebasepractice.databinding.ActivitySettingsBinding
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SettingsActivity : AppCompatActivity() {
    private lateinit var storage:FirebaseStorage
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage
        binding.backArrowSett.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.saveSett.setOnClickListener {
            var username = binding.etUser.text.toString()
            var status = binding.etAbout.text.toString()

            val obj: HashMap<String,Any> = HashMap()
            obj.put("username",username)
            obj.put("status",status)

            database.getReference().child("Users").child(auth.uid.toString()).updateChildren(obj)
            Toast.makeText(this,"Updated Successfully",Toast.LENGTH_LONG).show()

        }
        database.getReference().child("Users").child(auth.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val users: Users? = snapshot.getValue(Users::class.java)
                if (!users?.profilePic.isNullOrEmpty()) {
                    Picasso.get().load(users!!.profilePic).placeholder(R.drawable.person_unk).into(binding.profileIcon)
                }
                    binding.etAbout.text = Editable.Factory.getInstance().newEditable(users?.status ?: "")
                    binding.etUser.text = Editable.Factory.getInstance().newEditable(users?.username ?: "")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.plus.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,33)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if(data.data != null){
                val sFile:Uri = data.data!!
                binding.profileIcon.setImageURI(sFile)
                var storageRef = storage.reference.child("Profile Pictures").child(auth.uid.toString())
                storageRef.putFile(sFile).addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener {  uri ->
                        database.getReference().child("Users").child(auth.uid.toString()).child("profilePic").setValue(uri.toString())
                        Toast.makeText(this,"Updated Successfully",Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
}

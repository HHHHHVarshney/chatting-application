package com.example.firebasepractice

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasepractice.databinding.ActivitySignUpBinding
import com.example.firebasepractice.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
    @Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating Account")
        progressDialog.setMessage("We're creating your account...")


        binding.bSignup.setOnClickListener {
            if(binding.eMail.text.toString().isEmpty()){
                binding.eMail.setError("Enter Your E-mail")
                return@setOnClickListener
            }
            if(binding.password.text.toString().isEmpty()){
                binding.password.setError("Enter Your Password")
                return@setOnClickListener
            }
            if(binding.userName.text.toString().isEmpty()){
                binding.userName.setError("Enter Your Password")
                return@setOnClickListener
            }
            progressDialog.show()
            auth.createUserWithEmailAndPassword(binding.eMail.text.toString(), binding.password.text.toString())
                .addOnCompleteListener(this) { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Authentication Accomplished.", Toast.LENGTH_SHORT).show()
                        val user: Users = Users(binding.userName.text.toString(),binding.eMail.text.toString(),binding.password.text.toString())
                        val uId:String = task.result.user!!.uid.toString()
                        database.getReference().child("Users").child(uId).setValue(user)
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, task.exception!!.message,
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.alreadyAccount.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}
package com.example.firebasepractice

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasepractice.databinding.ActivitySignInBinding
import com.example.firebasepractice.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
//    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var progressDialog: ProgressDialog
    lateinit var progressDialog2: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Login")
        progressDialog.setMessage("Logging In...")
        progressDialog2 = ProgressDialog(this)
        progressDialog2.setTitle("Google Sign In")
        progressDialog2.setMessage("Creating Account...")

        binding.bSignin.setOnClickListener {
            if(binding.eMail.text.toString().isEmpty()){
                binding.eMail.setError("Enter Your E-mail")
                return@setOnClickListener
            }
            if(binding.password.text.toString().isEmpty()){
                binding.password.setError("Enter Your Password")
                return@setOnClickListener
            }
            progressDialog.show()
            auth.signInWithEmailAndPassword(binding.eMail.text.toString(),binding.password.text.toString()).addOnCompleteListener(this){ task ->
                progressDialog.dismiss()
                if(task.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,task.exception!!.message,Toast.LENGTH_LONG).show()
                }
            }

        }
        binding.clickForSignUp.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.btnGoogle.setOnClickListener {
//            progressDialog2.show()
            signIn()
        }

        val currentUser = auth.currentUser
        if(currentUser != null){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
        }


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


    }
    var RC_SIGN_IN = 65
    private fun signIn(){
        val signInIntent:Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account :GoogleSignInAccount = task.getResult(ApiException::class.java);
                Log.d("TAG","firebaseAuthWithGoogle:"+account.id)
                firebaseAuthWithGoogle(account.idToken.toString())
            }catch (e: ApiException){
                Log.w("TAG","Google Sign In Failed",e)
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){task ->
            if(task.isSuccessful){
                progressDialog2.dismiss()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                Log.d("TAG","Success")
                val user : FirebaseUser? = auth.currentUser
                var users: Users = Users(user?.displayName.toString(),user?.email.toString(),"")
                users.profilePic = user?.photoUrl.toString()
                database.getReference().child("Users").child(user?.uid.toString()).setValue(users)

            }
            else{
                Log.w("TAG","Sign In With Credential Faliure",task.exception)
            }
        }
    }
}
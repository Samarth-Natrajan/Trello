package com.example.trello.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.databinding.ActivitySignInBinding
import com.example.trello.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

@Suppress ("DEPRECATION")
class SignInActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding?.toolbarSignInActivity?.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        auth = FirebaseAuth.getInstance()
        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
    }
    private fun signInRegisteredUser(){
        val email = binding?.etEmail?.text?.toString()?.trim{it<=' '}.toString()
        val password = binding?.etPassword?.text?.toString().toString()
        if(validateForm(email,password)){
            showProgressDialog("Please Wait...")
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
                task ->
                hideProgressDialog()
                if(task.isSuccessful){
                    Log.d("signin","signInwithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this@SignInActivity,MainActivity::class.java))
                }
                else{
                    Log.w("signin","sigin with email:Failure",task.exception)
                    Toast.makeText(this@SignInActivity,task.exception?.message.toString(),Toast.LENGTH_LONG).show()
                }
            }

        }
    }
    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else ->{
                true
            }
        }
    }
}
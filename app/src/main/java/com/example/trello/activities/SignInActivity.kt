package com.example.trello.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.databinding.ActivitySignInBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.User
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
        binding?.btnShow?.setOnClickListener {
            if(binding?.btnShow?.text?.toString()=="SHOW"){
                binding?.etPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding?.btnShow?.text = "HIDE"
            }
            else{
                binding?.etPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
                binding?.btnShow?.text = "Show"
            }
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
                    FirestoreClass().loadUserData(this)
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

    fun signInSuccess(loggedInUser: User?) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity,MainActivity::class.java))
        finish()

    }
}
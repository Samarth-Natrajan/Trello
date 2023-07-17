package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.databinding.ActivitySignUpBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

@Suppress ("DEPRECATION")
class SignUpActivity : BaseActivity() {
    private var binding:ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding?.btnSignUp?.setOnClickListener {
            registerUser()
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

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
    }
    private fun registerUser(){
        val name = binding?.etName?.text?.toString()?.trim{it<=' '}.toString()
        val email = binding?.etEmail?.text?.toString()?.trim{it<=' '}.toString()
        val password = binding?.etPassword?.text?.toString().toString()
        if(validateForm(name,email,password)){
            showProgressDialog("Please Wait...")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail)
                    FirestoreClass().registerUser(this@SignUpActivity,user)

                }
                else{
                    hideProgressDialog()
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
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

    fun userRegisteredSuccess() {
        Toast.makeText(this,"You have successfully registered",Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}
package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.databinding.ActivitySignUpBinding
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
            showProgressDialog("Please Wait")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
                hideProgressDialog()
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    Toast.makeText(this,"${name} you have successfully registered the email address ${registeredEmail}",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()


                }
                else{
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
}
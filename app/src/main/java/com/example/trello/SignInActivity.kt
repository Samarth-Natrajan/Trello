package com.example.trello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.trello.databinding.ActivitySignInBinding
import com.example.trello.databinding.ActivitySignUpBinding
@Suppress ("DEPRECATION")
class SignInActivity : AppCompatActivity() {
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

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
    }
}
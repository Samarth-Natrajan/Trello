package com.example.trello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.trello.databinding.ActivitySignUpBinding
@Suppress ("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
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

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
    }
}
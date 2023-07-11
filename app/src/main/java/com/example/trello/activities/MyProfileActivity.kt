package com.example.trello.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.databinding.ActivityMyProfileBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MyProfileActivity : BaseActivity() {
    private var binding:ActivityMyProfileBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FirestoreClass().loadUserData(this)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionbar.title = "My Profile"
        }
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    fun setUserDataInUI(user:User){
        var profilepic = findViewById<CircleImageView>(R.id.user_image)
        Glide.with(this@MyProfileActivity).load(user.image).centerCrop().placeholder(R.drawable.user_place_holder).into(profilepic)
        binding?.profileName?.setText(user.name)
        binding?.profileEmail?.setText(user.email)
        if(user.mobile!=0L){
            binding?.profileMobile?.setText(user.mobile.toString())
        }
    }
}
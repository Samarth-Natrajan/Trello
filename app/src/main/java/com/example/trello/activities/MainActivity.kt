package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.databinding.ActivityMainBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var binding:ActivityMainBinding?=null
    companion object{
        const val MY_PROFILE_REQUEST:Int = 11
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        setupActionBar()
        var nav = findViewById<NavigationView>(R.id.nav_view)
        nav.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this)

    }
    private fun setupActionBar(){
        var actbar:Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(actbar)
        actbar.setNavigationIcon(R.drawable.action_nav_menu)
        actbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else{
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        when(item.itemId){

            R.id.nav_myProfile -> {
                Toast.makeText(this@MainActivity,"My Profile",Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(this@MainActivity,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetais(loggedInUser: User?) {
        var userImage = findViewById<CircleImageView>(R.id.iv_user_image)
        Glide.with(this).load(loggedInUser?.image).centerCrop().placeholder(R.drawable.user_place_holder).into(userImage)
        var userName = findViewById<TextView>(R.id.tv_username)
        userName.text = loggedInUser?.name

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK&&requestCode== MY_PROFILE_REQUEST){
            FirestoreClass().loadUserData(this)
        }
        else{
            Log.e("Canceled","CAncelled")
        }
    }
}
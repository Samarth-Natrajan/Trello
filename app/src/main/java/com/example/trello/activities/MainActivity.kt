package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.adapters.BoardItemsAdapter
import com.example.trello.databinding.ActivityMainBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.Board
import com.example.trello.models.User
import com.example.trello.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParsePosition

@Suppress ("DEPRECATION")
class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val MY_PROFILE_REQUEST:Int = 11
        const val CREATE_BOARD_REQUEST_CODE:Int = 12
    }
    private lateinit var mUsername:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        var nav = findViewById<NavigationView>(R.id.nav_view)
        nav.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this,true)
        val addbtn = findViewById<FloatingActionButton>(R.id.fab_create_board)
        addbtn.setOnClickListener {
            val intent = Intent(this,CreateBoardActivity::class.java).putExtra(Constants.NAME,mUsername)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

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

    fun updateNavigationUserDetais(loggedInUser: User?,readBoardList: Boolean) {
        mUsername = loggedInUser?.name.toString()
        var userImage = findViewById<CircleImageView>(R.id.iv_user_image)
        Glide.with(this).load(loggedInUser?.image).centerCrop().placeholder(R.drawable.user_place_holder).into(userImage)
        var userName = findViewById<TextView>(R.id.tv_username)
        userName.text = loggedInUser?.name
        if(readBoardList){
            showProgressDialog("Please Wait...")
            FirestoreClass().getBoardsList(this)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK&&requestCode== MY_PROFILE_REQUEST){
            FirestoreClass().loadUserData(this)
        }
        else if(resultCode == Activity.RESULT_OK&&requestCode== CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)
        }
        else{
            Log.e("Canceled","CAncelled")
        }
    }
    fun populateBoardsListToUI(boardsList:ArrayList<Board>){
        hideProgressDialog()
        if(boardsList.size>0){
            val tv_noboard = findViewById<TextView>(R.id.tv_no_boards_available)
            val rv = findViewById<RecyclerView>(R.id.rv_boards_list)
            rv.visibility = View.VISIBLE
            tv_noboard.visibility = View.GONE
            rv.layoutManager = LinearLayoutManager(this)
            rv.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this,boardsList)
            rv.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int,model:Board){
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        }
        else{
            val tv_noboard = findViewById<TextView>(R.id.tv_no_boards_available)
            val rv = findViewById<RecyclerView>(R.id.rv_boards_list)
            rv.visibility = View.GONE
            tv_noboard.visibility = View.VISIBLE
        }
    }
}
package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.databinding.ActivityCreateBoardBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.Board
import com.example.trello.models.User
import com.example.trello.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

@Suppress ("DEPRECATION")

class CreateBoardActivity : BaseActivity() {
    private val pickImage = 100
    private var binding:ActivityCreateBoardBinding?=null
    private var imageUri:Uri?=null
    private var mBoardImage:String=""
    private lateinit var mUserName:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title = "Create Board"
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding?.ivBoardImage?.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,pickImage)
        }
        binding?.btnCreate?.setOnClickListener {
            if(imageUri!=null){
                uploadBoardImage()
            }
            else{
                showProgressDialog("Please Wait...")
                createBoard()
            }
        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val profilepic = findViewById<CircleImageView>(R.id.iv_board_image)
        if(resultCode == RESULT_OK && requestCode==pickImage) {
            imageUri = data?.data
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(getCurrentUserID()).get().addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    //binding?.userImage?.setImageURI(imageUri)
                    //var profilepic = findViewById<CircleImageView>(R.id.user_image)
                    try{
                        Glide.with(this@CreateBoardActivity)
                            .load(imageUri)
                            .centerCrop().placeholder(R.drawable.board_place_holder)
                            .into(profilepic)
                    }
                    catch (e:IOException){
                        e.printStackTrace()
                    }

                }
        }
    }
    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        var board = Board(binding?.etBoardName?.text.toString(),mBoardImage,mUserName,assignedUsersArrayList)
        FirestoreClass().createBoard(this,board)
    }
    private fun uploadBoardImage(){
        showProgressDialog("Please Wait...")
        if(imageUri!=null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("UserImage"+System.currentTimeMillis()+"."+Constants.getFileExtension(this@CreateBoardActivity,imageUri))
            sRef.putFile(imageUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase board image url",taskSnapshot?.metadata?.reference?.downloadUrl?.toString().toString())
                taskSnapshot?.metadata!!.reference!!.downloadUrl!!.addOnSuccessListener {
                        uri ->
                    Log.i("downloadable image uri",uri.toString())
                    mBoardImage = uri?.toString().toString()
                    createBoard()


                }?.addOnFailureListener{
                        exception ->
                    Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }
}




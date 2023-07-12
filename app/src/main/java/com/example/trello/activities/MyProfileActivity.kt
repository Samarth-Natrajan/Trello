package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide

import com.example.trello.R
import com.example.trello.databinding.ActivityMyProfileBinding
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.User
import com.example.trello.utils.Constants
import com.google.common.io.Files.getFileExtension
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private val pickImage = 100
    private var imageUri: Uri?=null
    private lateinit var mUserDetails:User
    private var mProfileImageUri:String=""
    private var binding:ActivityMyProfileBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FirestoreClass().loadUserData(this)
        binding?.userImage?.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,pickImage)
        }
        binding?.btnUpdate?.setOnClickListener {
            if(imageUri!=null){
                uploadUserImage()
            }
            else{
                showProgressDialog("Please Wait..")
                updateUserProfileData()
            }
        }
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
        mUserDetails = user
        var profilepic = findViewById<CircleImageView>(R.id.user_image)
        Glide.with(this@MyProfileActivity).load(user.image).centerCrop().placeholder(R.drawable.user_place_holder).into(profilepic)
        binding?.profileName?.setText(user.name)
        binding?.profileEmail?.setText(user.email)
        if(user.mobile!=0L){
            binding?.profileMobile?.setText(user.mobile.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var profilepic = findViewById<CircleImageView>(R.id.user_image)
        if(resultCode == RESULT_OK && requestCode==pickImage) {
            imageUri = data?.data
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(getCurrentUserID()).get().addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    //binding?.userImage?.setImageURI(imageUri)
                    var profilepic = findViewById<CircleImageView>(R.id.user_image)
                    try{
                    Glide.with(this@MyProfileActivity).load(imageUri).centerCrop().placeholder(R.drawable.user_place_holder).into(profilepic)}
                    catch (e:IOException){
                        e.printStackTrace()
                    }

                }
        }
    }
    private fun uploadUserImage(){
        showProgressDialog("Please Wait")
        if(imageUri!=null){
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("UserImage"+System.currentTimeMillis()+"."+getFileExtension(imageUri))
            sRef.putFile(imageUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase image url",taskSnapshot?.metadata?.reference?.downloadUrl?.toString().toString())
                taskSnapshot?.metadata!!.reference!!.downloadUrl!!.addOnSuccessListener {
                    uri ->
                    Log.i("downloadable image uri",uri.toString())
                    mProfileImageUri = uri?.toString().toString()
                    updateUserProfileData()


                }?.addOnFailureListener{
                    exception ->
                    Toast.makeText(this@MyProfileActivity,exception.message,Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }
    private fun getFileExtension(uri:Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun updateUserProfileData(){
        val userHashmap = HashMap<String,Any>()
        var change = false
        if(mProfileImageUri.isNotEmpty()&&mProfileImageUri!=mUserDetails.image){
            userHashmap[Constants.IMAGE] = mProfileImageUri
            change = true
        }
        if(binding?.profileName?.text?.toString().toString()!=mUserDetails.name){
            userHashmap[Constants.NAME] = binding?.profileName?.text?.toString().toString()
            change = true
        }
        if(binding?.profileMobile?.text?.toString().toString()!=mUserDetails.mobile.toString()){
            userHashmap[Constants.MOBILE] = binding?.profileMobile?.text?.toString().toString().toLong()
            change = true
        }
        FirestoreClass().updateUserProfileData(this,userHashmap)


    }
}


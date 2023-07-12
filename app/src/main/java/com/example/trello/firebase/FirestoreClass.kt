package com.example.trello.firebase
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trello.activities.MainActivity
import com.example.trello.activities.MyProfileActivity
import com.example.trello.activities.SignInActivity
import com.example.trello.activities.SignUpActivity
import com.example.trello.models.User
import com.example.trello.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity,userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID()).set(userInfo,
            SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }
    fun getCurrentUserID():String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser!=null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
    fun updateUserProfileData(activity: MyProfileActivity,userHashMap:HashMap<String,Any>){
        mFirestore.collection(Constants.USERS).document(getCurrentUserID()).update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"profile data updated")
                Toast.makeText(activity,"Successfully Updated.",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()

            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"error while updating")
            }


    }
    fun  loadUserData(activity: Activity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID()).get().addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetais(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser!!)
                    }
                }

            }.addOnFailureListener{
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }

                }
            }
    }

}



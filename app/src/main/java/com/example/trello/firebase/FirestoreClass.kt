package com.example.trello.firebase
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trello.activities.*
import com.example.trello.models.Board
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

    fun createBoard(activity: CreateBoardActivity,board: Board){
        mFirestore.collection(Constants.BOARDS).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board created successfully")
                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exceprion ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"error")
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

            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error while updating")
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID())
            .get().addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)
                    board?.documentId = i.id
                    boardList.add(board!!)
                }
                activity.populateBoardsListToUI(boardList)
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }


    fun  loadUserData(activity: Activity,readBoardsList:Boolean=false){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID()).get().addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetais(loggedInUser,readBoardsList)
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

    fun getBoardsDetails(activity: TaskListActivity, documentId: String) {
        mFirestore.collection(Constants.BOARDS).document(documentId)
            .get().addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,document.toString())
                activity.boardDetails(document.toObject(Board::class.java)!!)
            }.addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }

}



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
                        activity//TODO
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
    fun addUpdateTaskList(activity: Activity,board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList Updated Successfully")
                if(activity is TaskListActivity)
                activity.addUpdateTaskListSuccess()
                else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }.addOnFailureListener{
                    e->
                if(activity is TaskListActivity)
                activity.hideProgressDialog()
                else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"Error while creating",e)
            }
    }

    fun getBoardsDetails(activity: TaskListActivity, documentId: String) {
        mFirestore.collection(Constants.BOARDS).document(documentId)
            .get().addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)

            }.addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }

    fun getAssignedMembersListDetails(activity: MembersActivity,assignedTo:ArrayList<String>){
        mFirestore.collection(Constants.USERS).whereIn(Constants.ID,assignedTo).get().addOnSuccessListener {
            document->
            Log.e(activity.javaClass.simpleName,document.documents.toString())
            val usersList:ArrayList<User> = ArrayList()
            for(i in document.documents){
                val user = i.toObject(User::class.java)
                usersList.add(user!!)
            }
            activity.setUpMembersList(usersList)
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating",e)
        }
    }
    fun getMemberDetails(activity: MembersActivity,email:String){
        mFirestore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email)
            .get().addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)
                    activity.memberDetails(user!!)
                }
                else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such Member Found!!")
                }
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,"Error while getting details",e
                )
            }
    }
    fun assignMemberToBoard(activity: MembersActivity,board: Board,user: User){
        val assignedToHashMap= HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFirestore.collection(Constants.BOARDS).document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(

                    activity.javaClass.simpleName,"Error while getting details",e
                )
            }
    }

}



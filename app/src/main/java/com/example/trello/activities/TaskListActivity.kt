package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.adapters.TaskListItemsAdapter
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.Board
import com.example.trello.models.Card
import com.example.trello.models.Task
import com.example.trello.utils.Constants
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog("Please Wait...")
        FirestoreClass().getBoardsDetails(this,boardDocumentId)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog("Please Wait...")
        FirestoreClass().getBoardsDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    fun updateTaskList(position:Int,listName:String,model:Task){
        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUsersList:ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserID())
        val card = Card(cardName,FirestoreClass().getCurrentUserID(),cardAssignedUsersList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun boardDetails(board: Board){
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()
        val addTaskList = Task("Add List")
        board.taskList.add(addTaskList)
        val rv:RecyclerView = findViewById(R.id.rv_task_list)
        rv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this,board.taskList)
        rv.adapter=adapter
    }
    private fun setupActionBar(){
        var actbar: Toolbar = findViewById(R.id.toolbar_task_list_activity)
        setSupportActionBar(actbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title = mBoardDetails.name
        }
        actbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
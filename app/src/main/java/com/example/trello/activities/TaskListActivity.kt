package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var mBoardDocumentId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog("Please Wait...")
        FirestoreClass().getBoardsDetails(this,mBoardDocumentId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK&&requestCode== MEMBERS_REQUEST_CODE||requestCode== CARD_DETAILS_REQUEST_CODE){
            showProgressDialog("Please Wait...")
            FirestoreClass().getBoardsDetails(this,mBoardDocumentId)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent,MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
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
    companion object{
        const val MEMBERS_REQUEST_CODE:Int = 13
        const val CARD_DETAILS_REQUEST_CODE:Int = 14

    }
}
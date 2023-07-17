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
import com.example.trello.models.Task
import com.example.trello.utils.Constants

class TaskListActivity : BaseActivity() {
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

    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)

        val addTaskList = Task("Add List")
        board.taskList.add(addTaskList)
        val rv:RecyclerView = findViewById(R.id.rv_task_list)
        rv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this,board.taskList)
        rv.adapter=adapter
    }
    private fun setupActionBar(title:String){
        var actbar: Toolbar = findViewById(R.id.toolbar_task_list_activity)
        setSupportActionBar(actbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title = title
        }
        actbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
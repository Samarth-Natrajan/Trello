package com.example.trello.activities

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.ActivityChooserView
import androidx.appcompat.widget.Toolbar
import com.example.trello.R
import com.example.trello.firebase.FirestoreClass
import com.example.trello.models.Board
import com.example.trello.models.Card
import com.example.trello.models.Task
import com.example.trello.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private var mtasklistposition = -1
    private var mcardlistposition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setupActionBar()
        findViewById<Button>(R.id.btn_update_card_details).setOnClickListener {
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this,"PLease enter a Card name",Toast.LENGTH_SHORT).show()
            }
        }

        var name_card = findViewById<EditText>(R.id.et_name_card_details)
        name_card.setText(mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name)
        name_card.setSelection(name_card.text.toString().length)
    }
    private fun setupActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionbar.title = mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mtasklistposition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)

        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mcardlistposition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun updateCardDetails(){
        val name = findViewById<EditText>(R.id.et_name_card_details)
        val card = Card(
            name.text.toString(),
            mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].createdBy,
            mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition].assignedTo
        )
        mBoardDetails.taskList[mtasklistposition].cards[mcardlistposition] = card
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        var cardList:ArrayList<Card> = mBoardDetails.taskList[mtasklistposition].cards
        cardList.removeAt(mcardlistposition)
        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mtasklistposition].cards = cardList
        showProgressDialog("Please Wait...")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)

    }
    private fun alertDialogForDeleteCard(cardName:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete card ${cardName}")
        builder.setIcon(R.drawable.alert_dialog_icon)
        builder.setPositiveButton("Yes"){dialogInterFace, which ->
            dialogInterFace.dismiss()
            deleteCard()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}